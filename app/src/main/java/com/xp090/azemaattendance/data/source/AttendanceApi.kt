package com.xp090.azemaattendance.data.source

import com.xp090.azemaattendance.data.model.Attendance
import com.xp090.azemaattendance.data.model.UserDailyAttendanceData
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*


interface AttendanceApi{
    @Multipart
    @POST("performCheck")
    fun submitCheck(
        @Query("userId") userId: String,
        @Query("checkType") checkType: String, @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double, @Part file: MultipartBody.Part
    ): Single<Attendance>

    @GET("fetchUserData")
    fun fetchUserData(@Query("userId") userId: String) : Single<UserDailyAttendanceData>
}