package com.xp090.azemaattendance.ui.main.checkinout


import androidx.lifecycle.MutableLiveData
import com.xp090.azemaattendance.data.model.Attendance
import com.xp090.azemaattendance.data.repository.AttendanceRepository
import com.xp090.azemaattendance.data.model.CheckActionData
import com.xp090.azemaattendance.ui.base.BaseViewModel
import com.xp090.azemaattendance.util.ext.schedule
import com.xp090.azemaattendance.util.ext.subscribeWithParsedError
import com.xp090.azemaattendance.util.extra.SingleLiveEvent
import java.io.File

class CheckInOutViewModel(private val attendanceRepository: AttendanceRepository) : BaseViewModel() {

    val attendanceData = attendanceRepository.attendanceData

    val checkEvent = SingleLiveEvent<CheckEvent>()
    val uiData = MutableLiveData<CheckActionData>()
    var imageFile: File? = null
    var checkActionData : CheckActionData? = null

    fun submitCheck(checkActionData: CheckActionData) {

        launch {
            uiData.value = checkActionData
            checkEvent.value = CheckEvent(isLoading = true)
            attendanceRepository.submitCheck(checkActionData, imageFile!!)
                .schedule()
                .subscribeWithParsedError({
                    checkEvent.postValue(CheckEvent(success = it))
                    deleteCachedImage()
                }, {
                    checkEvent.postValue(CheckEvent(error = it))
                })
        }
    }
    fun deleteCachedImage() {
        imageFile?.delete()
        imageFile = null
    }
}

data class CheckEvent(val isLoading: Boolean = false, val success: Attendance? = null, val error: String? = null)
