package com.xp090.azemaattendance.util.ext

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Use SchedulerProvider configuration for Single
 */
fun <T> Single<T>.schedule(): Single<T> = observeOn(AndroidSchedulers.mainThread()).subscribeOn(
    Schedulers.io())

fun Completable.schedule(): Completable = observeOn(AndroidSchedulers.mainThread()).subscribeOn(
    Schedulers.io())