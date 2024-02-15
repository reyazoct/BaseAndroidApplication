package com.reyaz.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.reyaz.utils.ErrorType
import com.reyaz.exception.ExceptionEmptyResponse
import com.reyaz.exception.ExceptionForbidden
import com.reyaz.exception.ExceptionNoInternet
import com.reyaz.exception.ExceptionOldVersion
import com.reyaz.exception.ExceptionServerDown
import com.reyaz.exception.ExceptionUnauthorised
import com.reyaz.repository.BaseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class BaseViewModel(app: Application) : AndroidViewModel(app) {
    val showToast = MutableSharedFlow<String>()

    private val _accessRevoked = MutableSharedFlow<Boolean>()
    val accessRevoked = _accessRevoked.asSharedFlow()

    suspend fun handleErrorAndReturn(throwable: Throwable): ErrorType? {
        val errorType = when (throwable) {
            is ExceptionNoInternet, is UnknownHostException, is ConnectException -> ErrorType.NoInternet
            is ExceptionServerDown -> ErrorType.ServerDown
            is ExceptionOldVersion -> ErrorType.OldVersion
            is SocketTimeoutException -> ErrorType.Timeout
            is ExceptionUnauthorised -> ErrorType.UnAuthorized
            is ExceptionForbidden -> ErrorType.Forbidden
            is ExceptionEmptyResponse -> ErrorType.EmptyResponse
            is HttpException -> {
                if (throwable.code() == 500) {
                    ErrorType.ServerError
                } else {
                    val errorMessage = getErrorMessage(throwable)
                    ErrorType.Generic(errorMessage)
                }
            }

            else -> ErrorType.Unknown(throwable)
        }
        if (errorType::class == accessRevokeErrorType::class) {
            _accessRevoked.emit(true)
            return null
        }
        return errorType
    }

    abstract fun getErrorMessage(throwable: HttpException): String

    fun <T : BaseRepository<*>> getRepository(entityClass: Class<T>): T {
        return BaseRepository.getInstance(entityClass)
    }

    abstract val accessRevokeErrorType: ErrorType

    protected val context: Context get() = getApplication<Application>().applicationContext
}
