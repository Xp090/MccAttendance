package com.xp090.azemaattendance.data.source

import android.content.Context
import android.content.SharedPreferences
import com.xp090.azemaattendance.data.model.Credentials

class LocalCredentialsDataSource(private val context: Context) {
    companion object {
        const val CREDENTIALS_SHARED_PREFERENCES = "credentialsSharedPreferences"
        const val USERNAME_KEY = "username_key"
        const val PASSWORD_KEY = "password_key"

    }

    private val sharedPreferences = context.getSharedPreferences(CREDENTIALS_SHARED_PREFERENCES, Context.MODE_PRIVATE)

    fun loadCredentials(): Credentials =
        Credentials(sharedPreferences.getString(USERNAME_KEY, null), sharedPreferences.getString(PASSWORD_KEY, null))


    fun saveCredentials(username: String?, password: String?) {
        sharedPreferences
            .edit()
            .apply {
                putString(USERNAME_KEY, username)
                putString(PASSWORD_KEY, password)
            }
            .apply()
    }

    fun saveCredentials(credentials: Credentials) {
        saveCredentials(credentials.username,credentials.password)
    }
    fun clearCredentials() {
        sharedPreferences
            .edit()
            .clear()
            .apply()
    }

}