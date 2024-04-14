package com.reyaz.repository

import android.util.Log
import com.reyaz.exception.ExceptionEmptyResponse
import com.reyaz.exception.ExceptionForbidden
import com.reyaz.exception.ExceptionOldVersion
import com.reyaz.exception.ExceptionServerDown
import com.reyaz.exception.ExceptionUnauthorised
import com.reyaz.helper.DataRequest
import retrofit2.HttpException
import retrofit2.Response

abstract class BaseRepository<AS> {

    private val client by lazy { createClient() }

    abstract fun createClient(): AS

    open suspend fun doBeforeTask() {}

    suspend fun <T> makeApiCall(call: suspend (apiService: AS) -> Response<T>): Response<T> {
        doBeforeTask()
        return call.invoke(client)
    }

    fun <T> responseToDataRequest(response: Response<T>): DataRequest<T> {
        return if (response.isSuccessful) {
            return response.body()?.let {
                DataRequest.Success(it)
            } ?: run {
                DataRequest.Failure(ExceptionEmptyResponse())
            }
        } else {
            val ex = when (response.code()) {
                401 -> ExceptionUnauthorised()
                403 -> ExceptionForbidden()
                502, 503 -> ExceptionServerDown()
                410 -> ExceptionOldVersion()
                else -> HttpException(response)
            }
            DataRequest.Failure(ex)
        }
    }

    companion object {
        private val instanceList = mutableListOf<BaseRepository<*>>()

        @Synchronized
        fun <T : BaseRepository<*>> getInstance(entityClass: Class<T>): T {
            return instanceList.firstOrNull {
                entityClass.isInstance(it)
            }?.let {
                it as? T ?: throw Exception("repository is not the same type")
            } ?: run {
                Log.i("Constructors", entityClass.constructors.toString())
                val instance = entityClass.getDeclaredConstructor().newInstance()
                instanceList.add(instance)
                instance
            }
        }

        @Synchronized
        fun clearAllRepositories() {
            instanceList.clear()
        }
    }
}
