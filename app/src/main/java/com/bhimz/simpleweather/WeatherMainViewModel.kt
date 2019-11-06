package com.bhimz.simpleweather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhimz.simpleweather.domain.model.Location
import com.bhimz.simpleweather.domain.model.LocationBindingModel
import com.bhimz.simpleweather.domain.repository.LocationRepository
import com.bhimz.simpleweather.domain.repository.WeatherRepository
import com.bhimz.simpleweather.util.PlaceUtil
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class WeatherMainViewModel : ViewModel(), KoinComponent {
    private val locationRepository: LocationRepository by inject()
    private val weatherRepository: WeatherRepository by inject()
    private val placeUtil: PlaceUtil by inject()

    private val _locations = MutableLiveData<List<LocationBindingModel>>().apply { value = listOf() }

    val locationList: LiveData<List<LocationBindingModel>> = _locations

    fun initLocations() = viewModelScope.launch {
        val currentLocation = getCurrentLocation()
        val locations = (currentLocation?.let { listOf(it) }
            ?: listOf()) + withContext(Dispatchers.IO) {
            locationRepository.getAllLocations().map {
                LocationBindingModel(it.locationName, it.latitude, it.longitude)
            }
        }
        _locations.value = locations
        //update weather information
        val updatedLocation = locations.map {
            try {
                updateWeather(it)
            } catch (e: Exception) {
                e.printStackTrace()
                it
            }
        }
        _locations.value = updatedLocation
    }

    private suspend fun getCurrentLocation() = placeUtil.findCurrentPlace()?.let {
        LocationBindingModel(it.locationName, it.latitude, it.longitude)
    }

    fun addNewLocation(location: Location) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            locationRepository.saveLocation(location)
        }
        val bindingModel = LocationBindingModel(location.locationName, location.latitude, location.longitude)
        val locationUpdate = _locations.value?.let {
            it + bindingModel
        } ?: listOf(bindingModel)
        _locations.value = locationUpdate
    }

    private suspend fun updateWeather(location: LocationBindingModel): LocationBindingModel {
        val weatherInfo = weatherRepository.getCurrentWeather(location.latitude, location.longitude)
        return location.copy(
            currentWeather = weatherInfo.name,
            weatherIconUrl = "http://openweathermap.org/img/wn/${weatherInfo.icon}@2x.png",
            temperature = weatherInfo.temperature
        )
    }
}