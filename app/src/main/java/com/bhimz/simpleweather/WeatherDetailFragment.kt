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
import com.bhimz.simpleweather.domain.model.WeatherBindingModel
import com.bhimz.simpleweather.util.HEADER_VIEW
import com.bhimz.simpleweather.util.ITEM_VIEW
import com.bhimz.simpleweather.util.ListItemModel
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*


class WeatherDetailFragment : Fragment() {

    private val args: WeatherDetailFragmentArgs by navArgs()
    private val locationName by lazy { args.locationName }
    private val latitude by lazy { args.latitude.toDouble() }
    private val longitude by lazy { args.longitude.toDouble() }
    private val weatherCondition by lazy { args.weatherCondition }
    private val weatherIconUrl by lazy { args.weatherIconUrl }
    private val temperature by lazy { args.temperature.toDouble() }

    private var itemList = listOf<ListItemModel>()

    private val viewModel: WeatherDetailViewModel by inject()

    private val weatherAdapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                HEADER_VIEW -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.view_weather_listheader, parent, false)
                    HeaderViewModel(view)
                }
                else -> {
                    val binding = DataBindingUtil.inflate<ViewWeatherListitemBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.view_weather_listitem, parent,
                        false
                    )
                    WeatherViewHolder(binding)
                }
            }

        }

        override fun getItemViewType(position: Int): Int = itemList[position].viewType

        override fun getItemCount(): Int = itemList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is WeatherViewHolder -> {
                    holder.binding.weather = itemList[position].itemData()
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

    class WeatherViewHolder(val binding: ViewWeatherListitemBinding) :
        RecyclerView.ViewHolder(binding.root)

    class HeaderViewModel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerTitleText: TextView = itemView.findViewById(R.id.headerTitleText)
    }
}
