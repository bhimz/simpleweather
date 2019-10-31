package com.bhimz.simpleweather

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    LocationRepositoryTest::class,
    WeatherActivityTest::class,
    LocationListActivityTest::class)
class AppTestSuite