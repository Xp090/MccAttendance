package com.xp090.azemaattendance.ui.base

import android.app.Application
import com.xp090.azemaattendance.di.repositoryModule
import com.xp090.azemaattendance.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BaseApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@BaseApp)
            modules(listOf(repositoryModule, viewModelModule))
        }

    }
}