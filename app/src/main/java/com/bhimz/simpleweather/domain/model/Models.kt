package com.bhimz.simpleweather.domain.model

data class Weather(
    val date: Long,
    val name: String,
    val temperature: Double,
    val icon: String? = null
)

data class Location(
    val locationName: String,
    val latitude: Double,
    val longitude: Double
)

data class LocationBindingModel(
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    var currentWeather: String = "",
    var weatherIconUrl: String? = null,
    var temperature: Double = 0.0
)

