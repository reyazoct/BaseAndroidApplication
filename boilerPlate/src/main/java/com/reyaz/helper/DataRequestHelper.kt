package com.reyaz.helper

import androidx.lifecycle.viewModelScope
import com.reyaz.viewmodel.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataRequestHelper<T> private constructor(
    private val coroutineScope: CoroutineScope,
    private val dataRequestCallback: suspend () -> DataRequest<T>,
    private val successCallback: suspend (data: T) -> Unit,
    private val failureCallback: suspend (th: Throwable) -> Unit,
) {
    private val coroutineExceptionHandler by lazy {
        CoroutineExceptionHandler { _, exception ->
            coroutineScope.launch {
                doOnFailure(exception)
            }
        }
    }

    private suspend fun doOnFailure(th: Throwable) {
        th.printStackTrace()
        failureCallback.invoke(th)
    }

    fun execute() {
        coroutineScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val dataRequest = dataRequestCallback.invoke()
            if (dataRequest is DataRequest.Success) {
                successCallback.invoke(dataRequest.data)
            } else if (dataRequest is DataRequest.Failure) {
                doOnFailure(dataRequest.throwable)
            }
        }
    }

    class Builder<T> {

        private val coroutineScope: CoroutineScope
        constructor(viewModel: BaseViewModel) {
            coroutineScope = viewModel.viewModelScope
        }

        constructor(coroutineScope: CoroutineScope) {
            this.coroutineScope = coroutineScope
        }

        private var dataRequestCallback: (suspend () -> DataRequest<T>)? = null
        private var successCallback: (suspend (data: T) -> Unit)? = null
        private var failureCallback: (suspend (th: Throwable) -> Unit)? = null

        fun setDataRequestCallback(callback: suspend () -> DataRequest<T>): Builder<T> {
            this.dataRequestCallback = callback
            return this
        }

        fun setSuccessCallback(callback: suspend (data: T) -> Unit): Builder<T> {
            this.successCallback = callback
            return this
        }

        fun setFailureCallback(callback: suspend (th: Throwable) -> Unit): Builder<T> {
            this.failureCallback = callback
            return this
        }

        fun build(): DataRequestHelper<T> {
            val finalDataCallback = dataRequestCallback ?: throw Exception("Data Callback cannot be null use setDataCallback")
            val finalSuccessCallback = successCallback ?: throw Exception("Success Callback cannot be null use setSuccessCallback")
            return DataRequestHelper(
                coroutineScope,
                finalDataCallback,
                finalSuccessCallback,
                failureCallback ?: {},
            )
        }
    }
}
