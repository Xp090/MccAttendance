package com.xp090.azemaattendance.ui.base

import android.app.Application
import com.xp090.azemaattendance.di.repositoryModule
import com.xp090.azemaattendance.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class BaseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidLogger()
            androidContext(this@BaseApp)
            modules(listOf(repositoryModule, viewModelModule))
        }
    }
}