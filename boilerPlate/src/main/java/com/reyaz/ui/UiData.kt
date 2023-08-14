package com.reyaz.ui

import com.reyaz.utils.ErrorType
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed class UiData<T> {
    class Success<T>(val data: T) : UiData<T>()
    class Loading<T> : UiData<T>()
    class Failure<T>(val errorType: ErrorType, val retry: () -> Unit) : UiData<T>()

    fun getBindData(): T? {
        return if (this is Success) {
            data
        } else {
            null
        }
    }
}
