package com.bhimz.simpleweather

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bhimz.simpleweather.di.appModule
import com.bhimz.simpleweather.di.netModule
import com.bhimz.simpleweather.di.testDbModule
import com.bhimz.simpleweather.domain.model.Location
import com.bhimz.simpleweather.util.PlaceUtil
import org.junit.After
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.test.KoinTest
//import org.mockito.Mockito


@RunWith(AndroidJUnit4::class)
class WeatherScreenTest : KoinTest {

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            androidContext(InstrumentationRegistry.getInstrumentation().context)
            modules(
                listOf(
                    appModule,
                    module {
                      single<PlaceUtil> {
                          object : PlaceUtil {
                              override suspend fun findCurrentPlace(): Location? {
                                  return Location("Jakarta Utara", 35.0, 105.11)
                              }
                          }
                          //Mockito.mock(PlaceUtil::class.java)
                      }
                    },
                    testDbModule,
                    netModule
                )
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

         /*   mock {
            onBlocking { findCurrentPlace() } doReturn Location("Jakarta Utara", 35.0, 105.11)
        }*/
       try {
           //val mockNavController = Mockito.mock(NavController::class.java)

           val scenario = launchFragmentInContainer {
               WeatherFragment().also { fragment ->
                   fragment.viewLifecycleOwnerLiveData.observeForever {
                       if (it != null) {
                           //Navigation.setViewNavController(fragment.requireView(), mockNavController)
                       }
                   }

               }
           }

           onView(withId(R.id.locationListView)).check(ViewAssertions.matches(isDisplayed()))
           Thread.sleep(3000)
           onView(withId(R.id.locationListView)).check { view, noViewFoundException ->
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