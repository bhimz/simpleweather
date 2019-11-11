package com.bhimz.simpleweather.domain.model

data class Weather(
    val date: Long,
    val name: String,
    val temperature: Double,
    val icon: String? = null
)

data class Location(
    val id: Int = 0,
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

