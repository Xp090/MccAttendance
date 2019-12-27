package com.xp090.azemaattendance.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.Observer
import com.xp090.azemaattendance.R
import com.xp090.azemaattendance.ui.login.LoginActivity
import com.xp090.azemaattendance.ui.main.MainActivity
import org.jetbrains.anko.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity() {

    private val splashViewModel:SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        fetchUserDataIfLoggedIn()

    }

    private fun fetchUserDataIfLoggedIn() {
        splashViewModel.fetchUserDataIfLoggedIn(resources!!.getInteger(R.integer.splash_screen_timeout))
            .observe(this, Observer { isLoggedIn ->
                if (isLoggedIn.success != null) {
                    if (isLoggedIn.success) startActivity<MainActivity>() else startActivity<LoginActivity>()
                    finishAffinity()
                }else{
                    alert(getString(R.string.error_with_retry,isLoggedIn.error),null ) {
                        yesButton { fetchUserDataIfLoggedIn() }
                        noButton {
                            startActivity<LoginActivity>()
                            finish()
                        }
                    }.show()
                }

            })
    }
}
