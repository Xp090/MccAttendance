package com.xp090.azemaattendance.data.repository

import androidx.lifecycle.MutableLiveData
import com.xp090.azemaattendance.data.model.Attendance
import com.xp090.azemaattendance.data.model.Credentials
import com.xp090.azemaattendance.data.model.User
import com.xp090.azemaattendance.data.model.UserDailyAttendanceData
import com.xp090.azemaattendance.data.source.AttendanceApi
import com.xp090.azemaattendance.data.source.FirebaseLoginDataSource
import com.xp090.azemaattendance.data.source.LocalCredentialsDataSource
import com.xp090.azemaattendance.util.Messages.USER_NOT_LOGGED_IN
import io.reactivex.Single

/**
 * Class that requests authentication and userData information from the remote data source and
 * maintains an in-memory cache of login status and userData credentials information.
 */

class UserDataRepository(
    private val dataSourceFirebase: FirebaseLoginDataSource,
    private val attendanceApi: AttendanceApi,
    private val localCredentialsDataSource: LocalCredentialsDataSource
) {


    var userData: MutableLiveData<User?> = MutableLiveData()
        private set
    var attendanceData: MutableLiveData<Attendance?> = MutableLiveData()
        private set

    var userCredentials: Credentials = localCredentialsDataSource.loadCredentials()
        private set

    fun logout() {
        dataSourceFirebase.logout()
        localCredentialsDataSource.clearCredentials()
        userCredentials.password = null
    }

    fun fetchUserDataIfLoggedIn(): Single<UserDailyAttendanceData> {
        return if (dataSourceFirebase.firebaseUser != null) {
            attendanceApi.fetchUserData(dataSourceFirebase.firebaseUser!!.uid)
                .doOnSuccess {
                    userData.postValue(it.user )
                    attendanceData.postValue(it.attendance)
                }
        } else {
            Single.error(Exception (USER_NOT_LOGGED_IN))
        }
    }

    fun login(credentials: Credentials): Single<UserDailyAttendanceData?> {
        return dataSourceFirebase.login(credentials)
            .flatMap { fetchUserDataIfLoggedIn() }
            .doOnSuccess { userCredentials = credentials.apply { localCredentialsDataSource.saveCredentials(this) } }
    }

}
