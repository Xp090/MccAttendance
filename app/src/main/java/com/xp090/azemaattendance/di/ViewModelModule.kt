package com.xp090.azemaattendance.di

import com.xp090.azemaattendance.ui.main.checkinout.CheckInOutViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val  viewModelModule = module {

    viewModel { CheckInOutViewModel(get()) }
}