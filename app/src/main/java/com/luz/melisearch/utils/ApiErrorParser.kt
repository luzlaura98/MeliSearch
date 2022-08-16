package com.luz.melisearch.utils

import android.content.Context
import com.luz.melisearch.R
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by Luz on 4/8/2022.
 */
fun Throwable.parseError(context: Context): String {
    return context.getString(
        when (this) {
            is UnknownHostException -> R.string.error_internet
            is SocketTimeoutException -> R.string.error_time_out
            else -> R.string.error_default_message
        }
    )
}