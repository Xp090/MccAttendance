package com.xp090.azemaattendance.ui.main.dailyreport

import androidx.lifecycle.ViewModel;
import com.xp090.azemaattendance.data.model.Attendance
import com.xp090.azemaattendance.data.repository.AttendanceRepository
import com.xp090.azemaattendance.ui.base.BaseViewModel
import com.xp090.azemaattendance.ui.main.checkinout.CheckEvent
import com.xp090.azemaattendance.util.ext.schedule
import com.xp090.azemaattendance.util.ext.subscribeWithParsedError
import com.xp090.azemaattendance.util.extra.SingleLiveEvent

class DailyReportViewModel(private val attendanceRepository: AttendanceRepository) : BaseViewModel() {

    val attendanceData = attendanceRepository.attendanceData
    val dailyReportEvent = SingleLiveEvent<DailyReportEvent>()

    fun submitDailyReport(report: String) {
        launch {
            dailyReportEvent.value = DailyReportEvent(isLoading = true)
            attendanceRepository.submitDailyReport(report)
                .schedule()
                .subscribeWithParsedError({
                    dailyReportEvent.value = DailyReportEvent(success = true)
                },{
                    dailyReportEvent.value = DailyReportEvent(error = it)
                })

        }
    }
}


data class DailyReportEvent(val isLoading: Boolean = false, val success: Boolean? = null, val error: String? = null)