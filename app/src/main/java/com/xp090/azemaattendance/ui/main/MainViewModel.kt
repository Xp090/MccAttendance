package com.xp090.azemaattendance.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.xp090.azemaattendance.data.repository.AttendanceRepository
import com.xp090.azemaattendance.data.repository.UserDataRepository
import com.xp090.azemaattendance.ui.base.BaseViewModel

class MainViewModel( val attendanceRepository: AttendanceRepository, val userDataRepository: UserDataRepository) : BaseViewModel() {
//    val defaultTabIndex: LiveData<Int> = Transformations
//        .map(attendanceRepository.attendanceData) {attendance->
//            return@map when {
//                attendance?.checkOut != null -> 2
//                attendance?.checkIn != null -> 1
//                else -> 0
//            }
//        }
}