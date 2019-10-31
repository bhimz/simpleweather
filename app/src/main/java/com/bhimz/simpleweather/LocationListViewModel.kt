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

    private val _locations = MutableLiveData<List<Location>>().apply { value = listOf() }

    val locationList: LiveData<List<Location>> = _locations

    fun initLocations() = viewModelScope.launch {
        val locations = withContext(Dispatchers.IO) { locationRepository.getAllLocations() }
        _locations.value = locations
    }
}