package com.bhimz.simpleweather

import android.app.Application
import com.bhimz.simpleweather.di.appModule
import com.bhimz.simpleweather.di.dbModule
import com.bhimz.simpleweather.di.netModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WeatherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WeatherApp)
            modules(listOf(appModule, netModule, dbModule))
        }
    }
}