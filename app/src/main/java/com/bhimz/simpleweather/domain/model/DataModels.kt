package com.bhimz.simpleweather.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocationData(
    @PrimaryKey
    @ColumnInfo(name = "location_name") val locationName: String,
    val latitude: Double,
    val longitude: Double
)