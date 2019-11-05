package com.bhimz.simpleweather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bhimz.simpleweather.databinding.FragmentWeatherDetailBinding
import com.bhimz.simpleweather.databinding.ViewWeatherListitemBinding
import com.bhimz.simpleweather.domain.model.LocationBindingModel
import com.bhimz.simpleweather.domain.model.Weather
import org.koin.android.ext.android.inject


class WeatherDetailFragment : Fragment() {

    private val args: WeatherDetailFragmentArgs by navArgs()
    private val locationName by lazy { args.locationName }
    private val latitude by lazy { args.latitude.toDouble() }
    private val longitude by lazy { args.longitude.toDouble() }
    private val weatherCondition by lazy { args.weatherCondition }
    private val weatherIconUrl by lazy { args.weatherIconUrl }
    private val temperature by lazy { args.temperature.toDouble() }

    private var weatherList = listOf<Weather>()

    private val viewModel: WeatherDetailViewModel by inject()

    private val weatherAdapter = object : RecyclerView.Adapter<WeatherViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
            val binding = DataBindingUtil.inflate<ViewWeatherListitemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.view_weather_listitem, parent,
                false
            )
            return WeatherViewHolder(binding)
        }

        override fun getItemCount(): Int = weatherList.size

        override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
            holder.binding.weather = weatherList[position]
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentWeatherDetailBinding.bind(
            inflater.inflate(
                R.layout.fragment_weather_detail,
                container,
                false
            )
        )
        binding.location = LocationBindingModel(
            locationName,
            latitude,
            longitude,
            weatherCondition,
            weatherIconUrl,
            temperature
        )
        val weatherListView = binding.weatherListView
        weatherListView.layoutManager = LinearLayoutManager(context)
        weatherListView.adapter = weatherAdapter
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.weatherList.observe(this, Observer(::onWeatherListUpdated))
        viewModel.loadForecasts(latitude, longitude)
    }

    private fun onWeatherListUpdated(weatherList: List<Weather>?) {
        weatherList ?: return
        this.weatherList = weatherList
        weatherAdapter.notifyDataSetChanged()
    }

    class WeatherViewHolder(val binding: ViewWeatherListitemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
