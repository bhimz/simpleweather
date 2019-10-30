package com.bhimz.simpleweather.di

import com.bhimz.simpleweather.BuildConfig
import com.bhimz.simpleweather.WeatherViewModel
import com.bhimz.simpleweather.domain.net.WeatherApi
import com.bhimz.simpleweather.domain.repository.WeatherRepository
import com.google.android.libraries.places.api.Places
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single {
        val ctx = androidContext()
        Places.initialize(ctx, BuildConfig.PLACES_API_KEY)
        Places.createClient(ctx)
    }
    factory {
        WeatherRepository(get())
    }
    viewModel {
        WeatherViewModel(get())
    }
}

val netModule = module {
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
}