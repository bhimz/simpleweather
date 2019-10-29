package com.bhimz.simpleweather.domain.model

import java.text.DecimalFormat
import java.util.*

data class Weather(
    val date: Long,
    val weather: String,
    val temperature: Double
) {
    private val monthNames = listOf(
        "JAN",
        "FEB",
        "MAR",
        "APR",
        "MAY",
        "JUN",
        "JUL",
        "AUG",
        "SEP",
        "OCT",
        "NOV",
        "DEC"
    )
    private val tempFormat = DecimalFormat("###.##")

    val dateText: String
        get() {
            val calendar = Calendar.getInstance()

            calendar.timeInMillis = date * 1000
            return "${monthNames[calendar.get(Calendar.MONTH)]} ${calendar.get(
                Calendar.DATE
            )}"

        }

    val temperatureText: String
        get() {
            val tempInCelcius = temperature - 273.15
            return "${tempFormat.format(tempInCelcius)}° C"
        }
}

