package com.bhimz.simpleweather

import com.bhimz.simpleweather.domain.appModule
import com.bhimz.simpleweather.domain.service.WeatherService
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class WeatherServiceTest: KoinTest {
    private val weatherService: WeatherService by inject()

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            modules(listOf(appModule))
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testGetWeather() {
        //given
        val cityName = "London"

        //when
        val weatherList = weatherService.getWeather(cityName)
        assertTrue("weather should not be null", weatherList != null)
        assertTrue("list should not be empty", weatherList!!.isNotEmpty())

    }
}
