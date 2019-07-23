package com.xp090.azemaattendance.di

import com.xp090.azemaattendance.repository.AttendanceApi
import com.xp090.azemaattendance.repository.AttendanceRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

val repositoryModule = module {

    single(createdAtStart = true) { createOkHttpClient() }

    single(createdAtStart = true) { createWebService<AttendanceApi>(get(),"http://192.168.1.103:8080/") }

    single { AttendanceRepository(get()) }

}

fun createOkHttpClient(): OkHttpClient {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return OkHttpClient.Builder()
//        .connectTimeout(10L, TimeUnit.SECONDS)
//        .readTimeout(10L, TimeUnit.SECONDS)
        .addInterceptor(httpLoggingInterceptor).build()
}

inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
    return retrofit.create(T::class.java)
}