package com.xp090.azemaattendance.data.type

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
//sealed class HttpResult<out T : Any> {
//
//    data class Success<out T : Any>(val data: T) : HttpResult<T>()
//    data class Error(val error: String) : HttpResult<T>()
//
//    override fun toString(): String {
//        return when (this) {
//            is Success<*> -> "Success[data=$data]"
//            is Error -> "Error[exception=$error]"
//        }
//    }
//}

data class HttpResult <out T : Any>(
    val success: T? = null,
    val error: String? = null
)
