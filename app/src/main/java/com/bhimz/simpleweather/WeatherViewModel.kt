package com.bhimz.simpleweather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhimz.simpleweather.domain.model.Weather
import com.bhimz.simpleweather.domain.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {
    private val _locationName = MutableLiveData<String>().apply { value = "" }
    private val _weatherList = MutableLiveData<List<Weather>>().apply { value = listOf() }

    val locationName: LiveData<String> = _locationName
    val weatherList: LiveData<List<Weather>> = _weatherList

    fun loadCurrentLocationWeather() = viewModelScope.launch {
        val weatherListUpdate = weatherRepository.getWeather(35.0, 139.0) ?: listOf()
        _weatherList.value = weatherListUpdate
    }
}