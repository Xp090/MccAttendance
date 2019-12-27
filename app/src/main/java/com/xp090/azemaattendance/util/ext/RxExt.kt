package com.xp090.azemaattendance.util.ext

import com.xp090.azemaattendance.data.type.ResponseErrorParser
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.ObjectHelper
import io.reactivex.internal.observers.ConsumerSingleObserver
import io.reactivex.schedulers.Schedulers
import org.koin.core.scope.Scope

/**
 * Use SchedulerProvider configuration for Single
 */
fun <T> Single<T>.schedule(): Single<T> =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun Completable.schedule(): Completable =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.subscribeWithParsedError(onSuccess: (t: T) -> Unit, onError: (e: String) -> Unit): Disposable {
    return subscribe(onSuccess, {
        val responseErrorParser = ResponseErrorParser()
        onError(responseErrorParser.parse(it))
    })
}

