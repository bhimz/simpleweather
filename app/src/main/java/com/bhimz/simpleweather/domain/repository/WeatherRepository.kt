package com.bhimz.simpleweather.domain.repository

import com.bhimz.simpleweather.domain.net.WeatherApi
import com.bhimz.simpleweather.domain.model.Weather

class WeatherRepository(
    private val weatherApi: WeatherApi
) {
    suspend fun getWeather(latitude: Double, longitude: Double): List<Weather>? {
        val response = weatherApi.getWeatherForecast(latitude, longitude, "b6907d289e10d714a6e88b30761fae22")
        return response.list.map {
            val weather = it.weather[0]
            Weather(it.dt, weather.main, it.main.temp)
        }
    }
}