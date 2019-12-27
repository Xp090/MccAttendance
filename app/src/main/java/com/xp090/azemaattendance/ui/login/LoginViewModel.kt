package com.xp090.azemaattendance.ui.login

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Patterns
import androidx.core.text.TextUtilsCompat
import com.xp090.azemaattendance.R
import com.xp090.azemaattendance.data.model.Credentials
import com.xp090.azemaattendance.data.repository.UserDataRepository
import com.xp090.azemaattendance.ui.base.BaseViewModel
import com.xp090.azemaattendance.util.ext.schedule
import com.xp090.azemaattendance.util.ext.subscribeWithParsedError
import timber.log.Timber


class LoginViewModel(private val userDataRepository: UserDataRepository) : BaseViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    val userCredentials = userDataRepository.userCredentials;

    fun login(username: String, password: String) {

        launch {
            userDataRepository.login(Credentials(username, password))
                .schedule()
                .subscribeWithParsedError({
                    _loginResult.postValue(LoginResult(success = true))
                }, {
                    _loginResult.postValue(LoginResult(error = it))
                    Timber.e(it)
                })
        }


    }

    fun loginDataChanged(username: String? = null, password: String? = null) {

        //  _loginForm.value = LoginFormState(isDataValid = false)
        var usernameError: Int? = null
        var passwordError: Int? = null



        if (!TextUtils.isEmpty(username) && !isUserNameValid(username!!)) {
            usernameError = R.string.invalid_username
        }
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password!!)) {
            passwordError = R.string.invalid_password
        }
        val isValid: Boolean = (usernameError == null && passwordError == null && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)  )
        _loginForm.value =
            LoginFormState(isDataValid = isValid, usernameError = usernameError, passwordError = passwordError)

    }


    private fun isUserNameValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()

    }


    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}


data class LoginResult(
    val success: Boolean = false,
    val error: String? = null
)

data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)
