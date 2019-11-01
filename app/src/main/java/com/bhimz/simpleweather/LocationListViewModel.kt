package com.bhimz.simpleweather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhimz.simpleweather.domain.model.Location
import com.bhimz.simpleweather.domain.repository.LocationRepository
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

class LocationListViewModel : ViewModel(), KoinComponent {
    private val locationRepository: LocationRepository by inject()
    private val placeUtil: PlaceUtil by inject()

    private val _locations = MutableLiveData<List<Location>>().apply { value = listOf() }

    val locationList: LiveData<List<Location>> = _locations

    fun initLocations() = viewModelScope.launch {
        val currentLocation = placeUtil.findCurrentPlace()
        val locations = (currentLocation?.let { listOf(it) }
            ?: listOf()) + withContext(Dispatchers.IO) { locationRepository.getAllLocations() }
        _locations.value = locations
    }
}