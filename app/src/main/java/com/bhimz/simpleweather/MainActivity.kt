package com.bhimz.simpleweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bhimz.simpleweather.domain.model.Weather
import com.bhimz.simpleweather.domain.service.WeatherService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_weather_listitem.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.text.DecimalFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var weatherList = listOf<Weather>()

    private val weatherService: WeatherService by inject()

    private val weatherAdapter = object : RecyclerView.Adapter<WeatherViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_weather_listitem, parent, false)
            return WeatherViewHolder(view)
        }

        override fun getItemCount(): Int = weatherList.size

        override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
            val weather = weatherList[position]
            holder.bind(weather)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        weatherListView.layoutManager = LinearLayoutManager(this)
        weatherListView.adapter = weatherAdapter
    }

    override fun onStart() {
        super.onStart()
        launch {
            weatherList = weatherService.getWeather(35.0, 139.0) ?: listOf()
            weatherAdapter.notifyDataSetChanged()
        }
    }

    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tempFormat by lazy { DecimalFormat("###.##") }
        private val monthNames by lazy { listOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC") }
        fun bind(weather: Weather) {
            itemView.weatherText.text = weather.weather
            val tempInCelcius = weather.temperature - 273.15
            itemView.temperatureText.text = "${tempFormat.format(tempInCelcius)}° C"
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = weather.date * 1000
            itemView.forecastDateText.text = "${monthNames[calendar.get(Calendar.MONTH)]} ${calendar.get(Calendar.DATE)}"
        }
    }
}
