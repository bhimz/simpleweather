package com.bhimz.simpleweather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bhimz.simpleweather.databinding.FragmentWeatherBinding
import com.bhimz.simpleweather.databinding.ViewLocationListitemBinding
import com.bhimz.simpleweather.domain.action.OnLocationClickListener
import com.bhimz.simpleweather.domain.model.Location
import org.koin.android.ext.android.inject

class WeatherFragment : Fragment() {
    private val permissionRequestCode = 1001

    private val viewModel: LocationListViewModel by inject()

    private var locationList: List<Location> = listOf()

    private val locationAdapter = object : RecyclerView.Adapter<LocationViewHolder>() {
        override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
            holder.binding.location = locationList[position]
            holder.binding.setOnLocationClickListener { location ->
                val action = WeatherFragmentDirections.actionOpenWeatherDetail(
                    location.locationName,
                    location.latitude.toFloat(),
                    location.longitude.toFloat()
                )
                this@WeatherFragment.findNavController().navigate(action)
            }
        }

        override fun getItemCount(): Int = locationList.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
            val binding = DataBindingUtil.inflate<ViewLocationListitemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.view_location_listitem,
                parent,
                false
            )
            return LocationViewHolder(binding)
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

    private fun onUpdateLocationList(locations: List<Location>?) {
        locations ?: return
        this.locationList = locations
        locationAdapter.notifyDataSetChanged()
    }

    class LocationViewHolder(val binding: ViewLocationListitemBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun ViewLocationListitemBinding.setOnLocationClickListener(block: (Location) -> Unit) {
        clickListener = object : OnLocationClickListener {
            override fun onClick(location: Location) {
                block(location)

            }
        }
    }
}
