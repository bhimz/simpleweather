package com.bhimz.simpleweather.domain.net

import com.bhimz.simpleweather.domain.model.ForecastData
import com.bhimz.simpleweather.domain.model.WeatherData
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast")
    suspend fun getWeatherForecast(@Query("lat") latitude: Double,
                                   @Query("lon") longitude: Double,
                                   @Query("appid") appId: String): ForecastData

    @GET("weather")
    suspend fun getCurrentWeather(@Query("lat") latitude: Double,
                                   @Query("lon") longitude: Double,
                                   @Query("appid") appId: String): WeatherData
}