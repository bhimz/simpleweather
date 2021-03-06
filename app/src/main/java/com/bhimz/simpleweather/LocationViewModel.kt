package com.bhimz.simpleweather

import androidx.lifecycle.*
import com.bhimz.simpleweather.domain.model.*
import com.bhimz.simpleweather.domain.repository.LocationRepository
import com.bhimz.simpleweather.domain.repository.WeatherRepository
import com.bhimz.simpleweather.util.LiveEvent
import com.bhimz.simpleweather.util.PlaceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.SimpleDateFormat
import java.util.*

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

    //in memory data source for weather and additional detail info
    private val weatherDataSource = mutableMapOf<Int, Weather>()
    private val detailDataSource = mutableMapOf<Int, AdditionalInfo>()

    private val dataUpdate = MutableLiveData<LiveEvent<Any>>()

    private val _locations = MediatorLiveData<List<LocationBindingModel>>()
    val locationList: LiveData<List<LocationBindingModel>> = _locations

    private val _uiState = MutableLiveData<UiState>().apply { value = InitialState() }
    val uiState: LiveData<UiState> = _uiState

    init {
        mergedLocation.addSource(currentLocation) {
            mergeLocationData(currentLocation, savedLocations)?.let {
                mergedLocation.value = it
            }
        }

        mergedLocation.addSource(savedLocations) {
            mergeLocationData(currentLocation, savedLocations)?.let {
                mergedLocation.value = it
            }
        }

        _locations.addSource(mergedLocation) { data ->
            val models = data.map { loc ->
                val w = weatherDataSource[loc.id]
                LocationBindingModel(
                    loc.id,
                    loc.locationName,
                    loc.latitude,
                    loc.longitude,
                    w?.name ?: "",
                    w?.icon?.let { "http://openweathermap.org/img/wn/${it}@2x.png" } ?: "",
                    w?.temperature ?: 0.0,
                    detailDataSource[loc.id] ?: AdditionalInfo()
                )
            }
            _locations.value = models
            val needUpdate =
                models.filter { it.latitude != 0.0 && it.longitude != 0.0 && it.currentWeather == "" }
            if (needUpdate.isNotEmpty()) {
                updateWeatherInfo(needUpdate)
            }
        }

        _locations.addSource(dataUpdate) { update ->
            update.getContentIfNotHandled()?.let { data ->
                val updatedLoc = when (data) {
                    is WeatherUpdate -> {
                        weatherDataSource[data.updateTo.id] = data.weather
                        _locations.value?.map {
                            if (it.id == data.updateTo.id) {
                                it.copy(
                                    currentWeather = data.weather.name,
                                    weatherIconUrl = "http://openweathermap.org/img/wn/${data.weather.icon}@2x.png",
                                    temperature = data.weather.temperature
                                )
                            } else it
                        }
                    }
                    is DetailStateUpdate -> {
                        val detail = (detailDataSource[data.locationId] ?: AdditionalInfo()).copy(
                            isCollapsed = data.isCollapsed
                        )
                        detailDataSource[data.locationId] = detail
                        _locations.value?.map {
                            if (it.id == data.locationId) {
                                it.copy(
                                    detail = detail
                                )
                            } else it
                        }
                    }
                    is ForecastUpdate -> {
                        val detail = (detailDataSource[data.locationId] ?: AdditionalInfo()).copy(
                            forecasts = data.forecasts
                        )
                        detailDataSource[data.locationId] = detail
                        _locations.value?.map {
                            if (it.id == data.locationId) {
                                it.copy(
                                    detail = detail
                                )
                            } else it
                        }
                    }
                    else -> null
                }
                updatedLoc?.let {
                    _locations.value = it
                }
            }
        }
    }

    private fun mergeLocationData(
        currentData: LiveData<Location>,
        savedData: LiveData<List<Location>>
    ): List<Location>? {
        val current = currentData.value
        val saved = savedData.value
        return if (current == null || saved == null) {
            null
        } else {
            listOf(current) + saved
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

    fun updateDetailState(location: LocationBindingModel, isCollapsed: Boolean) =
        viewModelScope.launch {
            dataUpdate.value = LiveEvent(DetailStateUpdate(location.id, isCollapsed))
            if (!isCollapsed && location.detail.forecasts == null) {
                loadForecasts(location)?.let {
                    dataUpdate.value = LiveEvent(ForecastUpdate(location.id, it))
                }
            }
        }

    private fun updateWeatherInfo(locations: List<LocationBindingModel>) =
        viewModelScope.launch {
            try {
                for (it in locations) {
                    val weather = weatherRepository.getCurrentWeather(it.latitude, it.longitude)
                    dataUpdate.value = LiveEvent(WeatherUpdate(weather, it))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = WeatherServiceUnavailableState()
            }
        }
    private suspend fun loadForecasts(location: LocationBindingModel): List<ForecastBindingModel>? {
        try {
            val weatherList = weatherRepository
                .getWeatherForecast(location.latitude, location.longitude) ?: return null
            val dateFormat = SimpleDateFormat("MMM dd", Locale.US)
            var currentTimeString = dateFormat.format(Date(System.currentTimeMillis()))
            val forecasts = mutableListOf<ForecastBindingModel>()
            weatherList.forEach {
                val timeString = dateFormat.format(Date(it.date * 1000))
                if (timeString != currentTimeString) {
                    currentTimeString = timeString
                    forecasts.add(
                        ForecastBindingModel(
                            it.date,
                            "http://openweathermap.org/img/wn/${it.icon}@2x.png",
                            it.temperature
                        )
                    )
                }
            }
            return forecasts

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}

class WeatherUpdate(val weather: Weather, val updateTo: LocationBindingModel)
class ForecastUpdate(val locationId: Int, val forecasts: List<ForecastBindingModel>)
class DetailStateUpdate(val locationId: Int, val isCollapsed: Boolean)

interface UiState
class InitialState : UiState
class UpdatingLocationState : UiState
class LocationUpdatedState : UiState
class LocationServiceUnavailableState : UiState
class WeatherServiceUnavailableState : UiState