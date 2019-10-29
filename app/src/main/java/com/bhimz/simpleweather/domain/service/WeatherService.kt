package com.bhimz.simpleweather.domain.service

import com.bhimz.simpleweather.domain.WeatherApi
import com.bhimz.simpleweather.domain.model.Weather

class WeatherService(
    private val weatherApi: WeatherApi
) {
    suspend fun getWeather(cityName: String, country: String): List<Weather>? {
        val response = weatherApi.getWeatherForecast("$cityName,$country", "b6907d289e10d714a6e88b30761fae22")
        return response.list.map {
            val weather = it.weather[0]
            Weather(cityName, it.dt, weather.main, it.main.temp)
        }
    }
}