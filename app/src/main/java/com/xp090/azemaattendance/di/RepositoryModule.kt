package com.xp090.azemaattendance.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.xp090.azemaattendance.data.source.AttendanceApi
import com.xp090.azemaattendance.data.repository.AttendanceRepository
import com.xp090.azemaattendance.data.repository.UserDataRepository
import com.xp090.azemaattendance.data.source.FirebaseLoginDataSource
import com.xp090.azemaattendance.data.source.LocalCredentialsDataSource
import com.xp090.azemaattendance.data.type.ResponseErrorParser
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import id.zelory.compressor.Compressor

val repositoryModule = module {

    single(createdAtStart = true) { createOkHttpClient() }

    single(createdAtStart = true) { createWebService<AttendanceApi>(get(),"https://us-central1-azeemaattendance.cloudfunctions.net/") }

    single { LocalCredentialsDataSource(androidContext()) }

    single { Compressor(androidContext()) }

    single { FirebaseLoginDataSource() }

    single { AttendanceRepository(get(),get(),get()) }

    single { UserDataRepository(get(),get(),get()) }



}

fun createOkHttpClient(): OkHttpClient {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return OkHttpClient.Builder()
        .connectTimeout(30L, TimeUnit.SECONDS)
        .readTimeout(30L, TimeUnit.SECONDS)
        .addInterceptor(httpLoggingInterceptor).build()
}

inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String): T {

    val moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .build()
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())).build()
    return retrofit.create(T::class.java)
}