package com.bhimz.simpleweather.util

import com.bhimz.simpleweather.domain.model.Location
import com.google.android.libraries.places.api.model.Place
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
            FindCurrentPlaceRequest.newInstance(listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG))
        return suspendCancellableCoroutine { cont ->
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
    }

}