package com.bhimz.simpleweather.util

class LiveEvent<out T>(private val data: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? =
        if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            data
        }

    fun peek(): T = data
}