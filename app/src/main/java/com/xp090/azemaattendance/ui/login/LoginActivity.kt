package com.xp090.azemaattendance.ui.login

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.google.android.material.snackbar.Snackbar
import com.xp090.azemaattendance.R
import com.xp090.azemaattendance.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.app.Activity
import androidx.core.content.ContextCompat.getSystemService
import android.view.inputmethod.InputMethodManager
import com.xp090.azemaattendance.util.ext.afterTextChanged


class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModel()

    private var foucedView:View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)


        loginViewModel.loginFormState.observe(this, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null && !username.isFocused) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null && !password.isFocused) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer
            loading.visibility = View.GONE
            login.isEnabled = true
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
                return@Observer
            }
            if (loginResult.success) {
                startActivity<MainActivity>()
            }

            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username = username.text.toString(),
                password = password.text.toString()
            )
        }

        username.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            foucedView = v
            if(!hasFocus)
                loginViewModel.loginDataChanged(
                    username = username.text.toString(),
                    password = password.text.toString()
                )
        }

        password.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
           foucedView = v
            if(!hasFocus)
                loginViewModel.loginDataChanged(
                    username = username.text.toString(),
                    password = password.text.toString()
                )
        }
        password.afterTextChanged {
            loginViewModel.loginDataChanged(
                username = username.text.toString(),
                password = password.text.toString()
            )
        }

        password.setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

    //    }
        login.setOnClickListener {
            val inputMethodManager = getSystemService(
                Activity.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                currentFocus?.windowToken, 0
            )
            login.isEnabled = false
            loading.visibility = View.VISIBLE
            loginViewModel.login(username.text.toString(), password.text.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        if (username.text.toString().trim().isBlank()) {
            loginViewModel.userCredentials.let {
                username.setText(it.username)
                password.setText(it.password)
                loginViewModel.loginDataChanged(it.username,it.password)
            }

        }
    }

    private fun showLoginFailed(errorString: String) {
        Snackbar.make(container, errorString, Snackbar.LENGTH_SHORT).show()
    }
}


