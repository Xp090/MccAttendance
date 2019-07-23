package com.xp090.azemaattendance.repository

import com.xp090.azemaattendance.repository.data.CheckActionData
import io.reactivex.Single
import okhttp3.MediaType
import java.io.File
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody


class AttendanceRepository(private val attendanceApi: AttendanceApi) {

    fun submitCheck(checkActionData: CheckActionData, imageFile: File): Single<ResponseBody> {
        val imageBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
        val imageMultiPart = MultipartBody.Part.createFormData("checkImage", imageFile.name,imageBody )
        return attendanceApi.submitCheck("testuser",checkActionData.checkType,
            checkActionData.latitude,
            checkActionData.longitude,
            imageMultiPart)
    }
}