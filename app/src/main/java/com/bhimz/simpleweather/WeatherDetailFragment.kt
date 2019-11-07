package com.bhimz.simpleweather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bhimz.simpleweather.domain.model.WeatherBindingModel
import com.bhimz.simpleweather.util.*
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.android.synthetic.main.fragment_weather_detail.view.*
import kotlinx.android.synthetic.main.view_weather_listitem.view.forecastDateText
import kotlinx.android.synthetic.main.view_weather_listitem.view.weatherText


class WeatherDetailFragment : Fragment() {

    private val args: WeatherDetailFragmentArgs by navArgs()
    private val locationName by lazy { args.locationName }
    private val latitude by lazy { args.latitude.toDouble() }
    private val longitude by lazy { args.longitude.toDouble() }

    private var itemList = listOf<ListItemModel>()

    private val viewModel: WeatherDetailViewModel by inject()
    private val weatherViewModel: WeatherMainViewModel by activityViewModels()

    private val weatherAdapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                HEADER_VIEW -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.view_weather_listheader, parent, false)
                    HeaderViewModel(view)
                }
                else -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.view_weather_listitem, parent, false)
                    WeatherViewHolder(view)
                }
            }

        }

        override fun getItemViewType(position: Int): Int = itemList[position].viewType

        override fun getItemCount(): Int = itemList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is WeatherViewHolder -> {
                    holder.bind(itemList[position].itemData())
                }
                is HeaderViewModel -> {
                    holder.headerTitleText.text = itemList[position].itemData()
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(
            R.layout.fragment_weather_detail,
            container,
            false
        )
        v.locationNameText.text = locationName
        /*v.weatherConditionText.text = weatherCondition
        v.temperatureText.text = String.format(
            getString(R.string.temperature_text),
            temperature - 273.25
        )
        v.weatherIconView.loadImage(weatherIconUrl)*/

        val weatherListView = v.weatherListView
        weatherListView.layoutManager = LinearLayoutManager(context)
        weatherListView.adapter = weatherAdapter
        return v
    }

    override fun onStart() {
        super.onStart()
        weatherViewModel.locationList.observe(this, Observer { locations ->
            locations.find { it.locationName == locationName }?.run {
                view?.weatherConditionText?.text = currentWeather
                view?.temperatureText?.text = String.format(
                    getString(R.string.temperature_text),
                    temperature - 273.15
                )
                view?.weatherIconView?.loadImage(weatherIconUrl)
            }
        })
        viewModel.weatherList.observe(this, Observer(::onWeatherListUpdated))
        viewModel.loadForecasts(latitude, longitude)
    }

    private fun onWeatherListUpdated(weatherList: List<WeatherBindingModel>?) {
        weatherList ?: return
        val items = mutableListOf<ListItemModel>()
        val headerFormat = SimpleDateFormat("MMM dd", Locale.US)
        var prevHeaderText = ""
        weatherList.forEach { weather ->
            val headerText = headerFormat.format(Date(weather.date * 1000))
            if (prevHeaderText != headerText) {
                items.add(ListItemModel(HEADER_VIEW, headerText))
                prevHeaderText = headerText
            }
            items.add(ListItemModel(ITEM_VIEW, weather))
        }
        this.itemList = items
        weatherAdapter.notifyDataSetChanged()
    }

    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(weather: WeatherBindingModel) {
            itemView.forecastDateText.formatDateText(weather.date, "HH:mm")
            itemView.findViewById<ImageView>(R.id.weatherIconView).loadImage(weather.weatherIconUrl)
            itemView.weatherText.text = weather.name
            val resource = itemView.resources
            itemView.findViewById<TextView>(R.id.temperatureText).text = String.format(
                resource.getString(R.string.temperature_text),
                weather.temperature - 273.25
            )
        }
    }

    class HeaderViewModel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerTitleText: TextView = itemView.findViewById(R.id.headerTitleText)
    }
}
