package com.bhimz.simpleweather.di

import androidx.room.Room
import com.bhimz.simpleweather.BuildConfig
import com.bhimz.simpleweather.LocationListViewModel
import com.bhimz.simpleweather.WeatherDetailViewModel
import com.bhimz.simpleweather.domain.db.AppDatabase
import com.bhimz.simpleweather.domain.net.WeatherApi
import com.bhimz.simpleweather.domain.repository.LocationRepository
import com.bhimz.simpleweather.domain.repository.WeatherRepository
import com.bhimz.simpleweather.util.PlaceUtil
import com.bhimz.simpleweather.util.PlaceUtilImpl
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
    single { get<AppDatabase>().locationDao() }
    factory {
        WeatherRepository(get())
    }
    factory {
        LocationRepository(get())
    }
    viewModel {
        WeatherDetailViewModel(get())
    }
    viewModel {
        LocationListViewModel(get(), get())
    }
}

val utilModule = module {
    single {
        val ctx = androidContext()
        Places.initialize(ctx, BuildConfig.PLACES_API_KEY)
        Places.createClient(ctx)
    }

    single<PlaceUtil> { PlaceUtilImpl(get()) }
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
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
    }
    single {
        (get<Retrofit>().create(WeatherApi::class.java))
    }
}

val dbModule = module {
    single { Room.databaseBuilder(get(), AppDatabase::class.java, "weather-db").build() }
}

val testDbModule = module {
    single { Room.inMemoryDatabaseBuilder(get(), AppDatabase::class.java).build() }
}