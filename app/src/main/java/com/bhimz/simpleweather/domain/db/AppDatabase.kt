package com.bhimz.simpleweather.domain.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bhimz.simpleweather.domain.db.dao.LocationDao
import com.bhimz.simpleweather.domain.model.LocationData

@Database(entities = [LocationData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}