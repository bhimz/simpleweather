package com.bhimz.simpleweather.domain.model

data class WeatherApiResponse(
    val list: List<ListItem>,
    val city: CityItem
) {
    data class ListItem(
        val dt: Long,
        val main: MainItem,
        val weather: List<WeatherItem>
    )
    data class CityItem(
        val name: String,
        val country: String
    )
    data class WeatherItem(
        val id: String,
        val main: String,
        val description: String
    )

    data class MainItem(
        val temp: Double
    )
}