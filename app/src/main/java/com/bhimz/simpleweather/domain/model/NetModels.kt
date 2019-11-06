package com.bhimz.simpleweather.domain.model

data class ForecastResponse(
    val list: List<WeatherResponse>,
    val city: CityItem
) {
    data class CityItem(
        val name: String,
        val country: String
    )
}

data class WeatherResponse(
    val dt: Long,
    val main: MainItem,
    val weather: List<WeatherItem>
) {

    data class WeatherItem(
        val id: String,
        val main: String,
        val description: String,
        val icon: String
    )

    data class MainItem(
        val temp: Double
    )
}