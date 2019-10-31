package com.bhimz.simpleweather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhimz.simpleweather.domain.model.Location
import com.bhimz.simpleweather.domain.repository.LocationRepository
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationListViewModel(
    private val locationRepository: LocationRepository,
    private val placesClient: PlacesClient
) : ViewModel() {

    private val _locations = MutableLiveData<List<Location>>().apply { value = listOf() }

    val locationList: LiveData<List<Location>> = _locations

    fun initLocations() = viewModelScope.launch {
        val placeRequest =
            FindCurrentPlaceRequest.newInstance(listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG))
        val currentLocation = suspendCancellableCoroutine<Location?> { cont ->
            placesClient.findCurrentPlace(placeRequest).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val placesLikelihood = task.result?.placeLikelihoods
                    if (!placesLikelihood.isNullOrEmpty() && placesLikelihood[0].place.latLng != null) {
                        val closest = placesLikelihood[0].place
                        val locationName = closest.address ?: ""
                        val latLng = closest.latLng
                        val latitude = latLng!!.latitude
                        val longitude = latLng.longitude
                        cont.resume(Location(locationName, latitude, longitude))
                    } else {
                        cont.resume(null)
                    }
                } else {
                    cont.resumeWithException(task.exception ?: Exception("cannot get location"))
                }
            }
        }
        val locations = (currentLocation?.let { listOf(it) }
            ?: listOf()) + withContext(Dispatchers.IO) { locationRepository.getAllLocations() }
        _locations.value = locations
    }
}