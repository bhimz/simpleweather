package com.bhimz.simpleweather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhimz.simpleweather.domain.model.Location
import com.bhimz.simpleweather.domain.repository.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationListViewModel(private val locationRepository: LocationRepository) : ViewModel() {

    private val _weatherList = MutableLiveData<List<Location>>().apply { value = listOf() }

    val weatherList: LiveData<List<Location>> = _weatherList

    fun initLocations() = viewModelScope.launch {
        val locations = withContext(Dispatchers.IO) { locationRepository.getAllLocations() }
        _weatherList.value = locations
    }
}