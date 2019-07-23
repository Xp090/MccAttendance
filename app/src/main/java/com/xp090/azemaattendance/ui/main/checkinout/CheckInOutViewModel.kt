package com.xp090.azemaattendance.ui.main.checkinout


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xp090.azemaattendance.repository.AttendanceRepository
import com.xp090.azemaattendance.repository.data.CheckActionData
import com.xp090.azemaattendance.ui.base.BaseViewModel
import com.xp090.azemaattendance.util.ext.schedule
import com.xp090.azemaattendance.util.extra.SingleLiveEvent
import java.io.File

class CheckInOutViewModel(private val attendanceRepository: AttendanceRepository) : BaseViewModel() {
    val checkEvent = SingleLiveEvent<CheckEvent>()
    val uiData = MutableLiveData<CheckActionData>()

    fun submitCheck(checkActionData: CheckActionData, file: File) {
        launch {
            uiData.value = checkActionData
            checkEvent.value = CheckEvent(isLoading = true)
            attendanceRepository.submitCheck(checkActionData, file)
                .schedule()
                .subscribe({
                    checkEvent.postValue(CheckEvent(success = it.string()))
                }, {
                    checkEvent.postValue(CheckEvent(error = it.message))
                })
        }
    }
}

data class CheckEvent(val isLoading: Boolean = false, val success: String? = null, val error: String? = null)
