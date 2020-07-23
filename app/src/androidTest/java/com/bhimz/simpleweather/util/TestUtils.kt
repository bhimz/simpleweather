package com.bhimz.simpleweather.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun <T> LiveData<T>.getOrAwaitValue(waitTime: Long = 3, timeUnit: TimeUnit = TimeUnit.SECONDS): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(t: T) {
            data = t
            latch.countDown()
            removeObserver(this)
        }
    }

    observeForever(observer)
    if (!latch.await(waitTime, timeUnit)) {
        throw Exception("Live Data value is never set")
    }
    return data ?: throw NullPointerException()
}

fun delayedTest(testDelay: Long = 300, maxRetry: Int = 10, testBlock: () -> Unit) =
    runBlocking {
        for (i in 1..maxRetry) {
            try {
                testBlock.invoke()
                break
            } catch (e: Throwable) {
                if (i == maxRetry) { throw e } else { delay(testDelay) }
            }
        }

    }