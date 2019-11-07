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
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0

    private var itemList = listOf<ListItemModel>()

    private val viewModel: ForecastViewModel by inject()
    private val weatherViewModel: LocationViewModel by activityViewModels()

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

        val weatherListView = v.weatherListView
        weatherListView.layoutManager = LinearLayoutManager(context)
        weatherListView.adapter = weatherAdapter

        val detailIndex = args.detailIndex
        weatherViewModel.locationList.observe(viewLifecycleOwner, Observer { locations ->
            if (detailIndex >= locations.size) return@Observer
            locations[detailIndex].run {
                view?.locationNameText?.text = locationName
                view?.weatherConditionText?.text = currentWeather
                view?.temperatureText?.text =
                    if (temperature == 0.0) resources.getString(R.string.double_dash)
                    else
                        String.format(
                            getString(R.string.temperature_text),
                            temperature - 273.15
                        )
                view?.weatherIconView?.loadImage(weatherIconUrl)
                if (latitude != currentLatitude || longitude != currentLongitude) { //handle if current location has changed
                    currentLatitude = latitude
                    currentLongitude = longitude
                    viewModel.loadForecasts(latitude, longitude)
                }
            }
        })
        viewModel.weatherList.observe(viewLifecycleOwner, Observer(::onWeatherListUpdated))

        return v
    }

    override fun onStart() {
        super.onStart()

    }

    private fun onWeatherListUpdated(weatherList: List<WeatherBindingModel>?) {
        weatherList ?: return
        val items = mutableListOf<ListItemModel>()
        val headerFormat = SimpleDateFormat("MMM dd", Locale.US)
        var prevHeaderText = ""
        weatherList.forEachIndexed { i, weather ->
            val headerText = headerFormat.format(Date(weather.date * 1000))
            if (prevHeaderText != headerText) {
                items.add(ListItemModel(-1, HEADER_VIEW, headerText))
                prevHeaderText = headerText
            }
            items.add(ListItemModel(i, ITEM_VIEW, weather))
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
