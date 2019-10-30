package com.bhimz.simpleweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bhimz.simpleweather.databinding.ActivityMainBinding
import com.bhimz.simpleweather.databinding.ViewWeatherListitemBinding
import com.bhimz.simpleweather.domain.model.Weather
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var weatherList = listOf<Weather>()

    private val viewModel: WeatherViewModel by inject()

    private val weatherAdapter = object : RecyclerView.Adapter<WeatherViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
            val binding = DataBindingUtil.inflate<ViewWeatherListitemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.view_weather_listitem, parent,
                false)
            return WeatherViewHolder(binding)
        }

        override fun getItemCount(): Int = weatherList.size

        override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
            holder.binding.weather = weatherList[position]
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mainBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val weatherListView = mainBinding.weatherListView
        weatherListView.layoutManager = LinearLayoutManager(this)
        weatherListView.adapter = weatherAdapter

        viewModel.weatherList.observe(this, Observer(::onWeatherListUpdated))
    }

    private fun onWeatherListUpdated(weatherList: List<Weather>?) {
        weatherList ?: return
        this.weatherList = weatherList
        weatherAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadCurrentLocationWeather()
    }

    class WeatherViewHolder(val binding: ViewWeatherListitemBinding) : RecyclerView.ViewHolder(binding.root)
}
