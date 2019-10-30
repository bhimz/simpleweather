package com.bhimz.simpleweather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bhimz.simpleweather.domain.model.Weather
import com.bhimz.simpleweather.domain.service.WeatherService

class WeatherViewModel(private val weatherService: WeatherService) : ViewModel() {
    private val _locationName = MutableLiveData<String>().apply { value = "" }
    private val _weatherList = MutableLiveData<List<Weather>>().apply { value = listOf() }

    val locationName: LiveData<String> = _locationName
    val weatherList: LiveData<List<Weather>> = _weatherList

    suspend fun loadCurrentLocationWeather() {
        val weatherListUpdate = weatherService.getWeather(35.0, 139.0) ?: listOf()
        _weatherList.value = weatherListUpdate
    }
}