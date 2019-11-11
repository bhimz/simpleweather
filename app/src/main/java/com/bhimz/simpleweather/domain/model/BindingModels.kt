package com.bhimz.simpleweather.domain.model

data class LocationBindingModel(
    val id: Int,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val currentWeather: String = "",
    val weatherIconUrl: String? = null,
    val temperature: Double = 0.0,
    val detail: AdditionalInfo = AdditionalInfo()
)

data class AdditionalInfo(
    val isCollapsed:Boolean = true,
    val forecasts: List<ForecastBindingModel>? = null
)

data class WeatherBindingModel(
    val date: Long,
    val name: String,
    val temperature: Double,
    val weatherIconUrl: String? = null
)

data class ForecastBindingModel(
    val date: Long,
    val weatherIconUrl: String,
    val temperature: Double
)