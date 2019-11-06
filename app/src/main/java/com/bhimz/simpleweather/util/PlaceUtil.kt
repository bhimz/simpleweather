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
            FindCurrentPlaceRequest.newInstance(listOf(Place.Field.ID))
        val placeId = suspendCancellableCoroutine<String?> { cont ->
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
                    cont.resume(match.place.id)
                    /*val placesLikelihood = task.result?.placeLikelihoods
                    if (!placesLikelihood.isNullOrEmpty() && placesLikelihood[0].place.latLng != null) {
                        val closest = placesLikelihood[0].place
                        var locationName = ""
                        closest.addressComponents?.asList()?.forEach {
                          if (it.types.contains("administrative_area_level_1")) locationName = it.name
                        }
                        //val locationName = closest.address ?: ""
                        val latLng = closest.latLng
                        val latitude = latLng!!.latitude
                        val longitude = latLng.longitude
                        cont.resume(Location(locationName, latitude, longitude))
                    } else {
                        cont.resume(null)
                    }*/
                } else {
                    cont.resumeWithException(task.exception ?: Exception("cannot get location"))
                }
            }
        }
        return placeId?.let {
            getLocationById(it)
        }
    }

    private suspend fun getLocationById(placeId: String): Location? =
        suspendCancellableCoroutine { cont ->
            val request = FetchPlaceRequest.newInstance(
                placeId,
                listOf(Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS)
            )
            placesClient.fetchPlace(request).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result == null) {
                        cont.resume(null)
                        return@addOnCompleteListener
                    }
                    val place = task.result!!.place
                    val latLng = place.latLng
                    val addressComponents = place.addressComponents
                    if (latLng == null || addressComponents == null) {
                        cont.resume(null)
                        return@addOnCompleteListener
                    } else {
                        var locationName = ""
                        addressComponents.asList().forEach {
                            if (it.types.contains("administrative_area_level_2")) locationName = it.shortName ?: it.name
                        }
                        cont.resume(
                            Location(locationName, latLng.latitude, latLng.longitude)
                        )
                    }
                } else {
                    cont.resumeWithException(task.exception ?: Exception("cannot get location"))
                }
            }
        }

}