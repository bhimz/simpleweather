package com.bhimz.simpleweather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bhimz.simpleweather.databinding.FragmentWeatherBinding
import com.bhimz.simpleweather.databinding.ViewLocationListitemBinding
import com.bhimz.simpleweather.domain.action.OnLocationClickListener
import com.bhimz.simpleweather.domain.model.LocationBindingModel
import com.bhimz.simpleweather.util.HEADER_VIEW
import com.bhimz.simpleweather.util.ITEM_VIEW
import com.bhimz.simpleweather.util.ListItemModel

class WeatherFragment : Fragment() {
    private val permissionRequestCode = 1001

    private val viewModel: WeatherMainViewModel by activityViewModels()

    private var listItems: List<ListItemModel> = listOf()

    private val locationAdapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is LocationViewHolder -> {
                    holder.binding.location = listItems[position].itemData()
                    holder.binding.setOnLocationClickListener { location ->
                        val action = WeatherFragmentDirections.actionOpenWeatherDetail(
                            location.locationName,
                            location.latitude.toFloat(),
                            location.longitude.toFloat(),
                            location.weatherIconUrl ?: "",
                            location.currentWeather,
                            location.temperature.toFloat()
                        )
                        this@WeatherFragment.findNavController().navigate(action)
                    }
                }
                is HeaderViewModel -> {
                    holder.headerTitleText.text = listItems[position].itemData()
                }
            }

        }

        override fun getItemViewType(position: Int): Int {
            return listItems[position].viewType
        }

        override fun getItemCount(): Int = listItems.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                HEADER_VIEW -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.view_location_listheader, parent, false)
                    HeaderViewModel(view)
                }
                else -> {
                    val binding = DataBindingUtil.inflate<ViewLocationListitemBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.view_location_listitem,
                        parent,
                        false
                    )
                    LocationViewHolder(binding)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentWeatherBinding.bind(
            inflater.inflate(
                R.layout.fragment_weather,
                container,
                false
            )
        )
        val recyclerView = binding.locationListView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = locationAdapter
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.locationList.observe(this, Observer(::onUpdateLocationList))
        context?.let {
            val requiredPermissions = mutableListOf<String>()
            if (ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requiredPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            if (ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (requiredPermissions.isNotEmpty()) {
                requestPermissions(requiredPermissions.toTypedArray(), permissionRequestCode)
            } else {
                viewModel.initLocations()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode && permissions.size == grantResults.filter { it == PackageManager.PERMISSION_GRANTED }.size) {
            viewModel.initLocations()
        }
    }

    private fun onUpdateLocationList(locations: List<LocationBindingModel>?) {
        locations ?: return
        val items = mutableListOf(ListItemModel(HEADER_VIEW, "Current Location"))
        locations.forEachIndexed { index, model ->
            if (index == 2) {
                items.add(ListItemModel(HEADER_VIEW, "Saved Locations"))
            }
            items.add(ListItemModel(ITEM_VIEW, model))
        }
        listItems = items
        locationAdapter.notifyDataSetChanged()
    }

    class LocationViewHolder(val binding: ViewLocationListitemBinding) :
        RecyclerView.ViewHolder(binding.root)

    class HeaderViewModel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerTitleText: TextView = itemView.findViewById(R.id.headerTitleText)
    }

    fun ViewLocationListitemBinding.setOnLocationClickListener(block: (LocationBindingModel) -> Unit) {
        clickListener = object : OnLocationClickListener {
            override fun onClick(location: LocationBindingModel) {
                block(location)

            }
        }
    }
}
