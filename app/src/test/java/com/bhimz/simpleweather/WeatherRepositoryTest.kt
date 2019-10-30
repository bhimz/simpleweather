package com.bhimz.simpleweather

import com.bhimz.simpleweather.di.appModule
import com.bhimz.simpleweather.di.netModule
import com.bhimz.simpleweather.domain.repository.WeatherRepository
import kotlinx.coroutines.runBlocking
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
class WeatherRepositoryTest: KoinTest {
    private val weatherRepository: WeatherRepository by inject()

    @Before
    fun setUp() {
        startKoin {
            modules(listOf(appModule, netModule))
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testGetWeather() {
        //given
        val lat = 35.0
        val lon = 139.0

        runBlocking {
            //when
            val weatherList = weatherRepository.getWeather(lat, lon)

            //then
            assertTrue("weather should not be null", weatherList != null)
            assertTrue("list should not be empty", weatherList!!.isNotEmpty())
        }

    }
}
