package com.bhimz.simpleweather

import androidx.lifecycle.*
import com.bhimz.simpleweather.domain.model.Location
import com.bhimz.simpleweather.domain.model.LocationBindingModel
import com.bhimz.simpleweather.domain.model.Weather
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


    private val currentLocation =
        MutableLiveData<Location>().apply {
            value = Location()
        }
    private val savedLocations = locationRepository.getAllLocations()
    private val mergedLocation = MediatorLiveData<List<Location>>()

    private val weatherMap = mutableMapOf<String, Weather>()
    private val weatherUpdate = MutableLiveData<WeatherUpdate>()

    private val _locations = MediatorLiveData<List<LocationBindingModel>>()
    val locationList: LiveData<List<LocationBindingModel>> = _locations

    private val _uiState = MutableLiveData<UiState>().apply { value = InitialState() }
    val uiState: LiveData<UiState> = _uiState

    init {
        mergedLocation.addSource(currentLocation) {
            mergedLocation.value = listOf(it) + (savedLocations.value ?: listOf())
        }

        mergedLocation.addSource(savedLocations) {
            mergedLocation.value = listOf(currentLocation.value ?: Location()) + it
        }

        _locations.addSource(mergedLocation) { data ->
            val models = data.map { loc ->
                val w = weatherMap[loc.locationName]
                LocationBindingModel(
                    loc.locationName,
                    loc.latitude,
                    loc.longitude,
                    w?.name ?: "",
                    w?.icon?.let { "http://openweathermap.org/img/wn/${it}@2x.png" } ?: "",
                    w?.temperature ?: 0.0
                )
            }
            _locations.value = models
            val needUpdate = models.filter { it.latitude != 0.0 && it.longitude != 0.0 && it.currentWeather == "" }
            if (needUpdate.isNotEmpty()) {
                updateWeatherInfo(needUpdate)
            }
        }

        _locations.addSource(weatherUpdate) { data ->
            weatherMap[data.updateTo.locationName] = data.weather
            val updatedLoc = _locations.value?.map {
                if (it.locationName == data.updateTo.locationName) {
                    it.copy(
                        currentWeather = data.weather.name,
                        weatherIconUrl = "http://openweathermap.org/img/wn/${data.weather.icon}@2x.png",
                        temperature = data.weather.temperature
                    )
                } else it
            } ?: return@addSource

            _locations.value = updatedLoc
        }
    }

    fun initCurrentLocation() = viewModelScope.launch {
        _uiState.value = UpdatingLocationState()
        val loc = try {
            placeUtil.findCurrentPlace()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        if (loc == null) {
            _uiState.value = LocationServiceUnavailableState()
            return@launch
        } else {
            currentLocation.value = loc
            _uiState.value = LocationUpdatedState()
        }
    }

    fun addNewLocation(location: Location) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            locationRepository.saveLocation(location)
        }
    }

    private fun updateWeatherInfo(locations: List<LocationBindingModel>) =
        viewModelScope.launch {
            try {
                for (it in locations) {
                    val weather = weatherRepository.getCurrentWeather(it.latitude, it.longitude)
                    weatherUpdate.value = WeatherUpdate(weather, it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = WeatherServiceUnavailableState()
            }
        }
}

class WeatherUpdate(val weather: Weather, val updateTo: LocationBindingModel)

interface UiState
class InitialState : UiState
class UpdatingLocationState : UiState
class LocationUpdatedState : UiState
class LocationServiceUnavailableState : UiState
class WeatherServiceUnavailableState: UiState