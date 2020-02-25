package com.xp090.azemaattendance.di


import com.xp090.azemaattendance.ui.login.LoginViewModel
import com.xp090.azemaattendance.ui.main.MainViewModel
import com.xp090.azemaattendance.ui.main.checkinout.CheckInOutViewModel
import com.xp090.azemaattendance.ui.main.dailyreport.DailyReportViewModel
import com.xp090.azemaattendance.ui.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val  viewModelModule = module {

    viewModel { CheckInOutViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { MainViewModel(get(),get()) }
    viewModel { DailyReportViewModel(get()) }
}