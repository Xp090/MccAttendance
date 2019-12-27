package com.xp090.azemaattendance.ui.splash

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xp090.azemaattendance.data.model.UserDailyAttendanceData
import com.xp090.azemaattendance.data.repository.UserDataRepository
import com.xp090.azemaattendance.data.type.HttpResult
import com.xp090.azemaattendance.ui.base.BaseViewModel
import com.xp090.azemaattendance.util.Messages.USER_NOT_LOGGED_IN
import com.xp090.azemaattendance.util.ext.schedule
import com.xp090.azemaattendance.util.ext.subscribeWithParsedError
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import java.util.concurrent.TimeUnit

class SplashViewModel constructor(private val userDataRepository: UserDataRepository) : BaseViewModel() {


    fun fetchUserDataIfLoggedIn(splashScreenTimeout: Int): LiveData<HttpResult<Boolean>> {
        val liveData: MutableLiveData<HttpResult<Boolean>> = MutableLiveData()

        launch {
            Single.zip(userDataRepository.fetchUserDataIfLoggedIn(),
                Single.timer(splashScreenTimeout.toLong(), TimeUnit.MILLISECONDS),
                BiFunction<UserDailyAttendanceData?, Long, Boolean> { _, _ -> true })
                .schedule()
                .subscribeWithParsedError({
                    liveData.postValue(HttpResult(success = it))
                }, {
                    if (it == USER_NOT_LOGGED_IN) {
                        liveData.postValue(HttpResult( success = false))
                    } else {
                        liveData.postValue(HttpResult( error =  it))
                    }

                })
        }
        return liveData
    }
}