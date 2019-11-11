package com.bhimz.simpleweather.domain.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.bhimz.simpleweather.domain.db.dao.LocationDao
import com.bhimz.simpleweather.domain.model.Location
import com.bhimz.simpleweather.domain.model.LocationData

class LocationRepository(private val locationDao: LocationDao) {
    suspend fun saveLocation(location: Location) =
        locationDao.insertLocationData(location.toLocationData())

    fun getAllLocations(): LiveData<List<Location>> {
        val mediator = MediatorLiveData<List<Location>>()
        mediator.addSource(locationDao.getAllLocations()) { value ->
            mediator.postValue(value.map { it.toLocation() })
        }
        return mediator
    }
}

fun Location.toLocationData(): LocationData = LocationData(id, locationName, latitude, longitude)

fun LocationData.toLocation(): Location = Location(id, locationName, latitude, longitude)
