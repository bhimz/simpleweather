package com.bhimz.simpleweather

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bhimz.simpleweather.di.appModule
import com.bhimz.simpleweather.domain.WeatherApi
import com.bhimz.simpleweather.domain.model.WeatherApiResponse
import com.google.gson.Gson
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest : KoinTest {

    @get:Rule
    val activityRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            androidContext(InstrumentationRegistry.getInstrumentation().context)
            modules(listOf(appModule, testNetModule))
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testLoadCurrentLocation() {
        Thread.sleep(3000)
        onView(withId(R.id.weatherListView)).check { view, noViewFoundException ->
            if (noViewFoundException != null) throw noViewFoundException
            val recyclerView = view as RecyclerView
            assertTrue("adapter should not be null", recyclerView.adapter != null)
            recyclerView.adapter?.run {
                assertTrue(itemCount > 0)
            }
        }

        onView(withRecyclerView(R.id.weatherListView)
            .atPosition(0))
            .check(matches(hasDescendant(withText("Clear"))))
        onView(withRecyclerView(R.id.weatherListView)
            .atPosition(0))
            .check(matches(hasDescendant(withText("10.61° C"))))
        onView(withRecyclerView(R.id.weatherListView)
            .atPosition(0))
            .check(matches(hasDescendant(withText("JAN 31"))))
    }


    private val testNetModule =  module {
        single {
            dummyApi
        }
    }

    val dummyResponse = "{\n" +
            "  \"cod\": \"200\",\n" +
            "  \"message\": 0.0082,\n" +
            "  \"cnt\": 40,\n" +
            "  \"list\": [\n" +
            "    {\n" +
            "      \"dt\": 1485799200,\n" +
            "      \"main\": {\n" +
            "        \"temp\": 283.76,\n" +
            "        \"temp_min\": 283.76,\n" +
            "        \"temp_max\": 283.761,\n" +
            "        \"pressure\": 1017.24,\n" +
            "        \"sea_level\": 1026.83,\n" +
            "        \"grnd_level\": 1017.24,\n" +
            "        \"humidity\": 100,\n" +
            "        \"temp_kf\": 0\n" +
            "      },\n" +
            "      \"weather\": [\n" +
            "        {\n" +
            "          \"id\": 800,\n" +
            "          \"main\": \"Clear\",\n" +
            "          \"description\": \"clear sky\",\n" +
            "          \"icon\": \"01n\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"clouds\": {\n" +
            "        \"all\": 0\n" +
            "      },\n" +
            "      \"wind\": {\n" +
            "        \"speed\": 7.27,\n" +
            "        \"deg\": 15.0048\n" +
            "      },\n" +
            "      \"rain\": {},\n" +
            "      \"sys\": {\n" +
            "        \"pod\": \"n\"\n" +
            "      },\n" +
            "      \"dt_txt\": \"2017-01-30 18:00:00\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"dt\": 1485810000,\n" +
            "      \"main\": {\n" +
            "        \"temp\": 282.56,\n" +
            "        \"temp_min\": 282.56,\n" +
            "        \"temp_max\": 282.563,\n" +
            "        \"pressure\": 1020.06,\n" +
            "        \"sea_level\": 1029.63,\n" +
            "        \"grnd_level\": 1020.06,\n" +
            "        \"humidity\": 100,\n" +
            "        \"temp_kf\": 0\n" +
            "      },\n" +
            "      \"weather\": [\n" +
            "        {\n" +
            "          \"id\": 800,\n" +
            "          \"main\": \"Clear\",\n" +
            "          \"description\": \"clear sky\",\n" +
            "          \"icon\": \"01n\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"clouds\": {\n" +
            "        \"all\": 0\n" +
            "      },\n" +
            "      \"wind\": {\n" +
            "        \"speed\": 6.21,\n" +
            "        \"deg\": 31.5035\n" +
            "      },\n" +
            "      \"rain\": {},\n" +
            "      \"sys\": {\n" +
            "        \"pod\": \"n\"\n" +
            "      },\n" +
            "      \"dt_txt\": \"2017-01-30 21:00:00\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"dt\": 1485820800,\n" +
            "      \"main\": {\n" +
            "        \"temp\": 282.3,\n" +
            "        \"temp_min\": 282.296,\n" +
            "        \"temp_max\": 282.3,\n" +
            "        \"pressure\": 1022.71,\n" +
            "        \"sea_level\": 1032.27,\n" +
            "        \"grnd_level\": 1022.71,\n" +
            "        \"humidity\": 100,\n" +
            "        \"temp_kf\": 0\n" +
            "      },\n" +
            "      \"weather\": [\n" +
            "        {\n" +
            "          \"id\": 800,\n" +
            "          \"main\": \"Clear\",\n" +
            "          \"description\": \"clear sky\",\n" +
            "          \"icon\": \"01d\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"clouds\": {\n" +
            "        \"all\": 0\n" +
            "      },\n" +
            "      \"wind\": {\n" +
            "        \"speed\": 6.71,\n" +
            "        \"deg\": 51.0002\n" +
            "      },\n" +
            "      \"rain\": {},\n" +
            "      \"sys\": {\n" +
            "        \"pod\": \"d\"\n" +
            "      },\n" +
            "      \"dt_txt\": \"2017-01-31 00:00:00\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"dt\": 1485831600,\n" +
            "      \"main\": {\n" +
            "        \"temp\": 282.27,\n" +
            "        \"temp_min\": 282.265,\n" +
            "        \"temp_max\": 282.27,\n" +
            "        \"pressure\": 1023.68,\n" +
            "        \"sea_level\": 1033.16,\n" +
            "        \"grnd_level\": 1023.68,\n" +
            "        \"humidity\": 100,\n" +
            "        \"temp_kf\": 0\n" +
            "      },\n" +
            "      \"weather\": [\n" +
            "        {\n" +
            "          \"id\": 800,\n" +
            "          \"main\": \"Clear\",\n" +
            "          \"description\": \"clear sky\",\n" +
            "          \"icon\": \"01d\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"clouds\": {\n" +
            "        \"all\": 0\n" +
            "      },\n" +
            "      \"wind\": {\n" +
            "        \"speed\": 5.46,\n" +
            "        \"deg\": 65.5\n" +
            "      },\n" +
            "      \"rain\": {},\n" +
            "      \"sys\": {\n" +
            "        \"pod\": \"d\"\n" +
            "      },\n" +
            "      \"dt_txt\": \"2017-01-31 03:00:00\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"dt\": 1485842400,\n" +
            "      \"main\": {\n" +
            "        \"temp\": 282.656,\n" +
            "        \"temp_min\": 282.656,\n" +
            "        \"temp_max\": 282.656,\n" +
            "        \"pressure\": 1023.75,\n" +
            "        \"sea_level\": 1033.22,\n" +
            "        \"grnd_level\": 1023.75,\n" +
            "        \"humidity\": 100,\n" +
            "        \"temp_kf\": 0\n" +
            "      },\n" +
            "      \"weather\": [\n" +
            "        {\n" +
            "          \"id\": 800,\n" +
            "          \"main\": \"Clear\",\n" +
            "          \"description\": \"clear sky\",\n" +
            "          \"icon\": \"01d\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"clouds\": {\n" +
            "        \"all\": 0\n" +
            "      },\n" +
            "      \"wind\": {\n" +
            "        \"speed\": 4.11,\n" +
            "        \"deg\": 84.0055\n" +
            "      },\n" +
            "      \"rain\": {},\n" +
            "      \"sys\": {\n" +
            "        \"pod\": \"d\"\n" +
            "      },\n" +
            "      \"dt_txt\": \"2017-01-31 06:00:00\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"city\": {\n" +
            "    \"id\": 1907296,\n" +
            "    \"name\": \"Tawarano\",\n" +
            "    \"coord\": {\n" +
            "      \"lat\": 35.0164,\n" +
            "      \"lon\": 139.0077\n" +
            "    },\n" +
            "    \"country\": \"none\"\n" +
            "  }\n" +
            "}"

    val dummyApi = object : WeatherApi {
        override suspend fun getWeatherForecast(
            latitude: Double,
            longitude: Double,
            appId: String
        ): WeatherApiResponse {
            return Gson().fromJson(dummyResponse, WeatherApiResponse::class.java)
        }

    }
}
