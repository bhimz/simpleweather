package com.bhimz.simpleweather.domain.db.dao

import androidx.room.Dao
import com.bhimz.simpleweather.domain.model.LocationData

@Dao
interface LocationDao {
    fun getLocationData(locationName: String): LocationData?

}