package com.bhimz.simpleweather.domain

import com.bhimz.simpleweather.domain.service.WeatherService
import org.koin.dsl.module

val appModule = module {
    factory {
        WeatherService()
    }
}