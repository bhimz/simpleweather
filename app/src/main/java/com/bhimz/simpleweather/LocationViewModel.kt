package com.bhimz.simpleweather

import androidx.lifecycle.*
import com.bhimz.simpleweather.domain.model.Location
import com.bhimz.simpleweather.domain.model.LocationBindingModel
import com.bhimz.simpleweather.domain.repository.LocationRepository
import com.bhimz.simpleweather.domain.repository.WeatherRepository
import com.bhimz.simpleweather.util.PlaceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class LocationViewModel : ViewModel(), KoinComponent {
    private val locationRepository: LocationRepository by inject()
    private val weatherRepository: WeatherRepository by inject()
    private val placeUtil: PlaceUtil by inject()

    private val _locations = MediatorLiveData<List<LocationBindingModel>>()
    private val savedLocations = MediatorLiveData<List<LocationBindingModel>>()
    private val currentLocation = MutableLiveData<LocationBindingModel>()
    private val _uiState = MutableLiveData<UiState>().apply { value = InitialState() }

    val locationList: LiveData<List<LocationBindingModel>> = _locations
    val uiState: LiveData<UiState> = _uiState

    init {
        _locations.addSource(currentLocation) { data ->
            _locations.value = listOf(data) + (savedLocations.value ?: listOf())
        }
        _locations.addSource(savedLocations) { data ->
            _locations.value =
                listOf(currentLocation.value ?:
                LocationBindingModel("", 0.0, 0.0)) + data
        }
        savedLocations.addSource(locationRepository.getAllLocations()) { data ->
            val models = data.map { LocationBindingModel(it.locationName, it.latitude, it.longitude) }
            savedLocations.postValue(models)
            viewModelScope.launch {
                try {
                    val updatedModels = models.map { updateWeather(it) }
                    savedLocations.postValue(updatedModels)
                } catch (e: Exception) {
                    e.printStackTrace()
                    _uiState.value = WeatherServiceUnavailableState()
                }
            }
        }
    }

    fun initCurrentLocation() = viewModelScope.launch {
        _uiState.value = UpdatingLocationState()
        val loc =
            try {
                getCurrentLocation()?.run { LocationBindingModel(locationName, latitude, longitude) }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        if (loc == null) {
            _uiState.value = LocationServiceUnavailableState()
        } else {
            currentLocation.value = loc
            try {
                currentLocation.value = updateWeather(loc)
                _uiState.value = LocationUpdatedState()
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = WeatherServiceUnavailableState()
            }
        }
    }

    private suspend fun getCurrentLocation() = placeUtil.findCurrentPlace()?.let {
        LocationBindingModel(it.locationName, it.latitude, it.longitude)
    }

    fun addNewLocation(location: Location) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            locationRepository.saveLocation(location)
        }
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

interface UiState
class InitialState : UiState
class UpdatingLocationState : UiState
class LocationUpdatedState : UiState
class LocationServiceUnavailableState : UiState
class WeatherServiceUnavailableState: UiState