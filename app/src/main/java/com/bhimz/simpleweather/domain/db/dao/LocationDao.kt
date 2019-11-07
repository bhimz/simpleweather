package com.bhimz.simpleweather.domain.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bhimz.simpleweather.domain.model.LocationData

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationData(locationData: LocationData)

    @Query("select * from LocationData where location_name=:locationName")
    suspend fun getLocationData(locationName: String): LocationData?

    @Query("select * from LocationData order by location_name")
    fun getAllLocations(): LiveData<List<LocationData>>

}