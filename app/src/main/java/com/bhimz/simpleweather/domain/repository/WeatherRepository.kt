package com.bhimz.simpleweather.domain.repository

import com.bhimz.simpleweather.domain.net.WeatherApi
import com.bhimz.simpleweather.domain.model.Weather

const val appId = "fef3526f985d15eb4971fd7235e6018d"//"b6907d289e10d714a6e88b30761fae22"

class WeatherRepository(
    private val weatherApi: WeatherApi
) {
    suspend fun getWeatherForecast(latitude: Double, longitude: Double): List<Weather>? {
        val response = weatherApi.getWeatherForecast(latitude, longitude, appId)
        return response.list.map {
            val weather = it.weather[0]
            Weather(it.dt, weather.main, it.main.temp)
        }
    }

    suspend fun getCurrentWeather(latitude: Double, longitude: Double): Weather {
        val response = weatherApi.getCurrentWeather(latitude, longitude, appId)
        return response.run {
            val w = weather[0].main
            Weather(dt, w, main.temp)
        }
    }
}