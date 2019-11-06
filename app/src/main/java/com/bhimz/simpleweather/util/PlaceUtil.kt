package com.bhimz.simpleweather.util

import com.bhimz.simpleweather.domain.model.Location
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface PlaceUtil {
    suspend fun findCurrentPlace(): Location?
}

class PlaceUtilImpl(private val placesClient: PlacesClient) : PlaceUtil {
    override suspend fun findCurrentPlace(): Location? {
        val placeRequest =
            FindCurrentPlaceRequest.newInstance(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        val place = suspendCancellableCoroutine<Place?> { cont ->
            placesClient.findCurrentPlace(placeRequest).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result == null) {
                        cont.resume(null)
                        return@addOnCompleteListener
                    }
                    val matches = task.result!!.placeLikelihoods
                    if (matches.isEmpty()) {
                        cont.resume(null)
                        return@addOnCompleteListener

                    }
                    val match = matches.toMutableList().apply { sortByDescending { it.likelihood } }[0]
                    cont.resume(match.place)
                } else {
                    cont.resumeWithException(task.exception ?: Exception("cannot get location"))
                }
            }
        }
        return place?.let {
            val latLng = it.latLng
            val name = it.name
            if (latLng == null || name == null) {
                null
            } else {
                Location(name, latLng.latitude, latLng.longitude)
            }
        }
    }

}