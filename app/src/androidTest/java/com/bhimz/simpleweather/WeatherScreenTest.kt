package com.bhimz.simpleweather

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bhimz.simpleweather.di.appModule
import com.bhimz.simpleweather.di.netModule
import com.bhimz.simpleweather.di.testDbModule
import com.bhimz.simpleweather.domain.db.dao.LocationDao
import com.bhimz.simpleweather.domain.model.Location
import com.bhimz.simpleweather.domain.model.LocationData
import com.bhimz.simpleweather.domain.model.WeatherData
import com.bhimz.simpleweather.domain.net.WeatherApi
import com.bhimz.simpleweather.util.PlaceUtil
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito


@RunWith(AndroidJUnit4::class)
class WeatherScreenTest : KoinTest {
    private lateinit var mockNavController: NavController
    private lateinit var currentLocation: Location
    private var savedLocations = listOf<Location>()
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        mockNavController = Mockito.mock(NavController::class.java)
        stopKoin()
        startKoin {
            androidContext(InstrumentationRegistry.getInstrumentation().context)
            modules(
                listOf()
            )
        }
        currentLocation = Location(0, "Jakarta Utara", 35.0, 105.11)
        savedLocations = emptyList()
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testOpenWeatherScreen() {
        openMainScreen()
        onView(withId(R.id.locationListView)).check(matches(isDisplayed()))
        onView(withId(R.id.locationListView)).check { view, noViewFoundException ->
            if (noViewFoundException != null) throw noViewFoundException
            view as RecyclerView
            assertTrue("should have adapter set", view.adapter != null)
            assertTrue("should have element inside ", view.adapter!!.itemCount > 0)
        }
        verifyCurrentLocationIsCorrect()
    }

    @Test
    fun testOpenWeatherScreenHaveSavedLocations() {
        savedLocations = listOf(
            Location(1, "Jakarta Selatan", 36.0, 105.11),
            Location(2, "Jakarta Barat", 25.0, 101.11)
        )
        openMainScreen()
        onView(withId(R.id.locationListView)).check(matches(isDisplayed()))
        onView(withId(R.id.locationListView)).check { view, noViewFoundException ->
            if (noViewFoundException != null) throw noViewFoundException
            view as RecyclerView
            assertTrue("should have adapter set", view.adapter != null)
            assertTrue("should have element inside ", view.adapter!!.itemCount > 0)
        }
        Thread.sleep(1000)
        verifyCurrentLocationIsCorrect()
        verifySavedLocationsAreCorrect()
    }

    /*@Test
    fun testClickWeatherDetail() {

        //given
        val currentLocation = Location(0,"Jakarta Utara", 35.0, 105.11)
        val mockPlaceUtil: PlaceUtil = mock {
            onBlocking { findCurrentPlace() } doReturn currentLocation
        }

        loadKoinModules(
            listOf(
                appModule,
                module {
                    single { mockPlaceUtil }
                },
                testDbModule,
                netModule
            )
        )

        try {
            val mockNavController = Mockito.mock(NavController::class.java)

            //when
            launchFragmentInContainer {
                WeatherFragment().also { fragment ->
                    fragment.viewLifecycleOwnerLiveData.observeForever {
                        if (it != null) {
                            Navigation.setViewNavController(
                                fragment.requireView(),
                                mockNavController
                            )
                        }
                    }
                }
            }

            Thread.sleep(1000)
            onView(withRecyclerView(R.id.locationListView).atPosition(0)).perform(click())

            verify(mockNavController).navigate(check<NavDirections> {
                val args = WeatherDetailFragmentArgs.fromBundle(it.arguments)
                assertEquals(
                    "detail index should match",
                    0,
                    args.detailIndex
                )
            })
        } catch (e: Exception) {
            e.printStackTrace()
            fail("error found: ${e.message}")
        }
    }

    @Test
    fun testOpenWeatherDetailScreen() {
        //given
        val locationName = "Jakarta Utara"
        val latitude = 35.0f
        val longitude = 105.11f

        val currentLocation = Location(0, "Jakarta Utara", 35.0, 105.11)
        val mockPlaceUtil: PlaceUtil = mock {
            onBlocking { findCurrentPlace() } doReturn currentLocation
        }

        val fragmentArgs = WeatherFragmentDirections.actionOpenWeatherDetail(0).arguments

        loadKoinModules(
            listOf(
                appModule,
                module {
                    single { mockPlaceUtil }
                },
                testDbModule,
                netModule
            )
        )



        try {
            val mockNavController = Mockito.mock(NavController::class.java)

            //when
            launchFragmentInContainer(fragmentArgs) {
                WeatherDetailFragment().also { fragment ->
                    fragment.viewLifecycleOwnerLiveData.observeForever {
                        if (it != null) {
                            Navigation.setViewNavController(
                                fragment.requireView(),
                                mockNavController
                            )
                        }
                    }
                }
            }

            //then
            onView(withId(R.id.weatherListView)).check(matches(isDisplayed()))
            Thread.sleep(3000)
            onView(withId(R.id.weatherListView)).check { view, noViewFoundException ->
                if (noViewFoundException != null) throw noViewFoundException
                view as RecyclerView
                assertTrue("should have adapter set", view.adapter != null)
                assertTrue("should have element inside ", view.adapter!!.itemCount > 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fail("error found: ${e.message}")
        }
    }*/

    private fun openMainScreen() {
        initModules()
        launchFragmentInContainer(Bundle(), R.style.AppTheme) {
            WeatherFragment().also { fragment ->
                fragment.viewLifecycleOwnerLiveData.observeForever {
                    if (it != null) {
                        Navigation.setViewNavController(
                            fragment.requireView(),
                            mockNavController
                        )
                    }
                }
            }
        }
    }

    private fun initModules() {
        val mockPlaceUtil: PlaceUtil = mock {
            onBlocking { findCurrentPlace() } doReturn currentLocation
        }
        val mockApi: WeatherApi = mock {
            onBlocking { getCurrentWeather(any(), any(), any()) } doAnswer {
                val lat = it.arguments[0] as Double
                val lon = it.arguments[1] as Double
                val id = "$lat@$lon"
                mockWeatherDataSet[id] ?: throw Exception("no matching weather data")
            }
        }
        loadKoinModules(
            listOf(
                appModule,
                module {
                    single { mockPlaceUtil }
                    single { mockApi }
                },
                testDbModule
            )
        )
        val locationDao: LocationDao by inject()
        runBlocking {
            savedLocations.forEach { d ->
                locationDao.insertLocationData(
                    LocationData(
                        id = d.id,
                        locationName = d.locationName,
                        latitude = d.latitude,
                        longitude = d.longitude
                    )
                )
            }
        }
    }

    private fun verifyCurrentLocationIsCorrect() {
        verifyLocationView(
            withRecyclerView(R.id.locationListView)
            .atPosition(1),
            currentLocation
        )
    }

    private fun verifySavedLocationsAreCorrect() {
        val locations = savedLocations.toMutableList()
        locations.sortBy { it.locationName }
        locations.forEachIndexed { i, location ->
            val itemMatcher =  withRecyclerView(R.id.locationListView)
                .atPosition(3 + i)
            verifyLocationView(itemMatcher, location)
        }
    }

    private fun verifyLocationView(
        itemMatcher: Matcher<View>,
        location: Location
    ) {
        val weather =
            mockWeatherDataSet["${location.latitude}@${location.longitude}"]
                ?: throw AssertionError("should have matching weather data")

        onView(itemMatcher).check(
            matches(
                hasDescendant(
                    withText(location.locationName)
                )
            )
        )
        onView(itemMatcher).check(
            matches(
                hasDescendant(
                    withText(weather.weatherCondition)
                )
            )
        )

        val expectedTemperature = String.format(
            context.getString(R.string.temperature_text),
            weather.temperature - 273.15
        )
        onView(itemMatcher).check(
            matches(
                hasDescendant(
                    withText(expectedTemperature)
                )
            )
        )
    }

    companion object {
        val mockWeatherDataSet = mapOf(
            "35.0@105.11" to WeatherData(
                1595473837,
                300.68,
                "Clouds",
                "04d"
            ),
            "36.0@105.11" to WeatherData(
                1595473837,
                310.68,
                "Sunny",
                "04d"
            ),
            "25.0@101.11" to WeatherData(
                1595473837,
                200.68,
                "Rainy",
                "04d"
            )
        )
    }

}