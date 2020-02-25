package com.xp090.azemaattendance.data.repository

import androidx.lifecycle.MutableLiveData
import com.xp090.azemaattendance.data.model.Attendance
import com.xp090.azemaattendance.data.model.CheckActionData
import com.xp090.azemaattendance.data.model.DailyReportRequest
import com.xp090.azemaattendance.data.source.AttendanceApi
import id.zelory.compressor.Compressor
import io.reactivex.Single
import okhttp3.MediaType
import java.io.File
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody


class AttendanceRepository(private val attendanceApi: AttendanceApi,
                           private val userDataRepository: UserDataRepository,
                           private val imageCompressor: Compressor) {

    val attendanceData: MutableLiveData<Attendance?>
        get() = userDataRepository.attendanceData

    fun submitCheck(checkActionData: CheckActionData, imageFile: File): Single<Attendance> {
        return imageCompressor.compressToFileAsFlowable(imageFile)
            .singleOrError()
            .flatMap {compressedImage->
                val imageBody = RequestBody.create(MediaType.parse("image/*"), compressedImage)
                val imageMultiPart = MultipartBody.Part.createFormData("checkImage", imageFile.name,imageBody )
                 attendanceApi.submitCheck(userDataRepository.userData.value!!.id,checkActionData.checkType,
                    checkActionData.latitude,
                    checkActionData.longitude,
                    imageMultiPart)
            }
            .doOnSuccess { attendanceData.postValue(it) }

    }

    fun submitDailyReport(dailyReport: String):Single<Any> {
        val dailyReportRequest = DailyReportRequest(dailyReport)

        return attendanceApi.submitDailyReport(userDataRepository.userData.value!!.id,dailyReportRequest)
    }
}