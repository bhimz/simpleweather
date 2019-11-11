package com.bhimz.simpleweather.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocationData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "location_name") val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)