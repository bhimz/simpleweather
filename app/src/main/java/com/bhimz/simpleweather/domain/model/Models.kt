package com.bhimz.simpleweather.domain.model

data class Weather(
    val city: String,
    val date: Long,
    val weather: String,
    val temperature: Double
)