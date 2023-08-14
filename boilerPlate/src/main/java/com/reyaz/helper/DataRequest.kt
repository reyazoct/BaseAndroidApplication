package com.reyaz.helper

import kotlin.contracts.contract

sealed class DataRequest<T> {
    data class Success<T>(val data: T) : DataRequest<T>()
    data class Failure<T>(val throwable: Throwable) : DataRequest<T>()

    fun isSuccess(): Boolean {
        contract {
            returns(true) implies (this@DataRequest is Success)
        }
        return this is Success
    }

    fun getBindingData(): T? {
        return when(this) {
            is Failure -> null
            is Success -> data
        }
    }
}
