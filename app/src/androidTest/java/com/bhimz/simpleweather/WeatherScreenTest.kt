package com.bhimz.simpleweather

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bhimz.simpleweather.di.appModule
import com.bhimz.simpleweather.di.netModule
import com.bhimz.simpleweather.di.testDbModule
import com.bhimz.simpleweather.domain.model.Location
import com.bhimz.simpleweather.util.PlaceUtil
import com.nhaarman.mockitokotlin2.*
import org.junit.After
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.Mockito


@RunWith(AndroidJUnit4::class)
class WeatherScreenTest : KoinTest {

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            androidContext(InstrumentationRegistry.getInstrumentation().context)
            modules(
                listOf()
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testOpenWeatherScreen() {
        //given
        val currentLocation = Location("Jakarta Utara", 35.0, 105.11)
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

            onView(withId(R.id.locationListView)).check(matches(isDisplayed()))
            Thread.sleep(1000)
            onView(withId(R.id.locationListView)).check { view, noViewFoundException ->
                if (noViewFoundException != null) throw noViewFoundException
                view as RecyclerView
                assertTrue("should have adapter set", view.adapter != null)
                assertTrue("should have element inside ", view.adapter!!.itemCount > 0)
            }
            onView(withRecyclerView(R.id.locationListView).atPosition(0))
                .check(matches(hasDescendant(withText(currentLocation.locationName))))
        } catch (e: Exception) {
            e.printStackTrace()
            fail("error found: ${e.message}")
        }
    }

    @Test
    fun testClickWeatherDetail() {
        //given
        val currentLocation = Location("Jakarta Utara", 35.0, 105.11)
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

        val currentLocation = Location("Jakarta Utara", 35.0, 105.11)
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
    }

}