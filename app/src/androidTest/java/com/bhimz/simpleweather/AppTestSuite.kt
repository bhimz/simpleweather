package com.bhimz.simpleweather

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    LocationRepositoryTest::class,
    MainActivityTest::class,
    LocationListActivityTest::class)
class AppTestSuite