package com.xp090.azemaattendance.data.type

import android.content.Context
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException

import org.koin.core.KoinComponent
import org.koin.core.get
import retrofit2.HttpException
import java.io.IOException
import java.lang.IllegalArgumentException


class ResponseErrorParser  :KoinComponent {
    private val context: Context  = get()
    fun parse(throwable: Throwable): String {
        val errorIdentifier: String? = when (throwable) {
            is HttpException -> throwable.response()?.errorBody()?.string()
            is IOException , is FirebaseNetworkException -> "NETWORK_ERROR"
            is FirebaseAuthException, is IllegalArgumentException  -> "INVALID_USERNAME_OR_PASSWORD"
            else -> null
        }

        if (errorIdentifier != null) {
            val stringResId = context.resources!!.getIdentifier(errorIdentifier,"string",context.packageName)
            if (stringResId != 0) {
                return context.resources!!.getString(stringResId)
            }
        }

        return  throwable.localizedMessage
    }
}