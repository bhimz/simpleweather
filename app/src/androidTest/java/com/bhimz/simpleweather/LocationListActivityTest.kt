package com.bhimz.simpleweather

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.bhimz.simpleweather.di.appModule
import com.bhimz.simpleweather.di.testDbModule
import com.bhimz.simpleweather.domain.model.Location
import com.bhimz.simpleweather.domain.repository.LocationRepository
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
@LargeTest
class LocationListActivityTest : KoinTest {

    private val locationRepository: LocationRepository by inject()

    @get:Rule
    val activityRule: ActivityTestRule<LocationListActivity> =
        ActivityTestRule(LocationListActivity::class.java, true, false)

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            androidContext(InstrumentationRegistry.getInstrumentation().context)
            modules(listOf(appModule, testDbModule))
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun tesOpenLocationList() {
        //given
        val locations = listOf(
            Location("Jakarta", -6.1, 106.5),
            Location("Bandung", -6.4, 106.8),
            Location("Surabaya", -5.4, 103.8)
        )

        runBlocking {
            locations.forEach {
                locationRepository.saveLocation(it)
            }
        }

        //when activity is launched
        activityRule.launchActivity(null)

        //then
        Espresso.onView(ViewMatchers.withId(R.id.locationListView))
            .check { view, noViewFoundException ->
                if (noViewFoundException != null) throw noViewFoundException
                val recyclerView = view as RecyclerView
                Assert.assertTrue("adapter should not be null", recyclerView.adapter != null)
                recyclerView.adapter?.run {
                    Assert.assertEquals("data size should match", locations.size, itemCount)
                }
            }

        val sortedLocations = locations.sortedBy { it.locationName }

        sortedLocations.forEachIndexed { index, location ->
            Espresso.onView(
                withRecyclerView(R.id.locationListView)
                    .atPosition(index)
            )
                .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(location.locationName))))
        }

    }
}