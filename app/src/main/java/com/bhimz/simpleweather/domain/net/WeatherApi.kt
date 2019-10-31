package com.bhimz.simpleweather.domain.net

import com.bhimz.simpleweather.domain.model.ForecastResponse
import com.bhimz.simpleweather.domain.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast")
    suspend fun getWeatherForecast(@Query("lat") latitude: Double,
                                   @Query("lon") longitude: Double,
                                   @Query("appid") appId: String): ForecastResponse

    @GET("weather")
    suspend fun getCurrentWeather(@Query("lat") latitude: Double,
                                   @Query("lon") longitude: Double,
                                   @Query("appid") appId: String): WeatherResponse
}