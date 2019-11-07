package com.bhimz.simpleweather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhimz.simpleweather.domain.model.Weather
import com.bhimz.simpleweather.domain.model.WeatherBindingModel
import com.bhimz.simpleweather.domain.repository.WeatherRepository
import kotlinx.coroutines.launch

class ForecastViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {
    private val _locationName = MutableLiveData<String>().apply { value = "" }
    private val _weatherList =
        MutableLiveData<List<WeatherBindingModel>>().apply { value = listOf() }

    val locationName: LiveData<String> = _locationName
    val weatherList: LiveData<List<WeatherBindingModel>> = _weatherList

    fun loadForecasts(latitude: Double, longitude: Double) = viewModelScope.launch {
        val weatherListUpdate = weatherRepository.getWeatherForecast(latitude, longitude)?.map {
            WeatherBindingModel(
                it.date,
                it.name,
                it.temperature,
                "http://openweathermap.org/img/wn/${it.icon}@2x.png"
            )
        } ?: listOf()
        _weatherList.value = weatherListUpdate
    }
}