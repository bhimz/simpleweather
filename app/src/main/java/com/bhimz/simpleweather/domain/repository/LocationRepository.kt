package com.bhimz.simpleweather.domain.repository

import com.bhimz.simpleweather.domain.db.dao.LocationDao
import com.bhimz.simpleweather.domain.model.Location
import com.bhimz.simpleweather.domain.model.LocationData

class LocationRepository(private val locationDao: LocationDao) {
    suspend fun saveLocation(location: Location) =
        locationDao.insertLocationData(location.toLocationData())

    suspend fun getAllLocations(): List<Location> =
        locationDao.getAllLocations().map { it.toLocation() }
}

fun Location.toLocationData(): LocationData = LocationData(locationName, latitude, longitude)

fun LocationData.toLocation(): Location = Location(locationName, latitude, longitude)
