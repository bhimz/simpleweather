package com.bhimz.simpleweather.di

import com.bhimz.simpleweather.domain.WeatherApi
import com.bhimz.simpleweather.domain.service.WeatherService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single<Interceptor> {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.HEADERS
        interceptor
    }
    single {
        OkHttpClient.Builder()
            .addInterceptor(get())
            .build() }
    single {
        Retrofit.Builder()
            .baseUrl("https://samples.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
    }
    single {
        (get<Retrofit>().create(WeatherApi::class.java))
    }
    factory {
        WeatherService(get())
    }
}