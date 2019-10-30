package com.bhimz.simpleweather

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.bhimz.simpleweather.di.appModule
import com.bhimz.simpleweather.domain.db.dao.LocationDao
import com.bhimz.simpleweather.domain.model.Location
import com.bhimz.simpleweather.domain.repository.LocationRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import java.lang.Exception
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules

@RunWith(AndroidJUnit4::class)
@SmallTest
class LocationRepositoryTest: KoinTest {
    private val locationRepository: LocationRepository by inject()
    private val locationDao: LocationDao by inject()

    @Before
    fun setUp() {
        loadKoinModules(listOf(appModule))
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testSaveLocation() {
        //given
        val locationName = "Jakarta"
        val lat = -6.117664
        val lon = 106.906349
        val location = Location(locationName, lat, lon)

        runBlocking {
            //when
            try {
                locationRepository.saveLocation(location)
                val savedLocationData = locationDao.getLocationData(locationName)
                assertTrue("saved location should not be null", savedLocationData != null)
                assertEquals("location name should match", locationName, savedLocationData!!.locationName)
                assertEquals("latitude should match", lat, savedLocationData.latitude, 0.0001)
                assertEquals("longitude should match", lon, savedLocationData.longitude, 0.0001)
            } catch (e: Exception) {
                e.printStackTrace()
                fail("should have no error, but received: ${e.message}")
            }
        }
    }
}