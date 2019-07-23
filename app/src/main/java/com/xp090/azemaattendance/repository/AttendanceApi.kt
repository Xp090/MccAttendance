package com.xp090.azemaattendance.repository

import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.POST
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Query


interface AttendanceApi{
    @Multipart
    @POST("performCheck")
    fun submitCheck(
        @Query("userId") userId: String,
        @Query("checkType") checkType: String, @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double, @Part file: MultipartBody.Part
    ): Single<ResponseBody>
}