package com.reyaz.utils

import android.content.Context
import com.reyaz.R

sealed class ErrorType {
    object NoInternet : ErrorType()
    object Timeout : ErrorType()
    object ServerDown : ErrorType()
    object OldVersion : ErrorType()
    object ServerError : ErrorType()
    object UnAuthorized : ErrorType()
    object NotAllowed : ErrorType()
    object EmptyResponse : ErrorType()
    data class Generic(val message: String) : ErrorType()
    data class Unknown(val th: Throwable) : ErrorType()

    fun getMessage(context: Context, debug: Boolean = false): String {
        return when (this) {
            is Generic -> message
            is NoInternet -> context.getString(R.string.no_internet)
            is NotAllowed -> context.getString(R.string.feature_not_allowed)
            is OldVersion -> context.getString(R.string.update_app_feature)
            is ServerDown -> context.getString(R.string.app_under_maintenance)
            is ServerError -> context.getString(R.string.something_went_wrong)
            is Timeout -> context.getString(R.string.request_timed_out)
            is EmptyResponse -> context.getString(R.string.empty_response)
            is Unknown -> {
                if (debug) th.message ?: context.getString(R.string.something_went_wrong)
                else context.getString(R.string.something_went_wrong)
            }
            else -> throw Exception("Invalid type")
        }
    }

    val allowRetry: Boolean
        get() = !(this is EmptyResponse || this is Generic)

    val isEmpty: Boolean
        get() = this is EmptyResponse
}
