package com.bhimz.simpleweather.domain.repository

import com.bhimz.simpleweather.domain.db.dao.LocationDao
import com.bhimz.simpleweather.domain.model.Location
import com.bhimz.simpleweather.domain.model.LocationData

class LocationRepository(private val locationDao: LocationDao) {
    suspend fun saveLocation(location: Location) =
        locationDao.insertLocationData(location.toLocationData())

    fun getAllLocations(): List<Location> = listOf()
}

fun Location.toLocationData(): LocationData {
    return LocationData(
        locationName, latitude, longitude
    )
}
