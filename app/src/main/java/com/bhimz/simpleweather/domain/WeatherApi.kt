package com.bhimz.simpleweather.domain

import com.bhimz.simpleweather.domain.model.WeatherApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast")
    suspend fun getWeatherForecast(@Query("lat") latitude: Double,
                                   @Query("lon") longitude: Double,
                                   @Query("appid") appId: String): WeatherApiResponse
}