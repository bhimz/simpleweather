package com.bhimz.simpleweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bhimz.simpleweather.databinding.ActivityLocationListBinding
import com.bhimz.simpleweather.databinding.ViewLocationListitemBinding
import com.bhimz.simpleweather.domain.model.Location
import org.koin.android.ext.android.inject

class LocationListActivity : AppCompatActivity() {

    private val viewModel: LocationListViewModel by inject()
    private var locationList: List<Location> = listOf()

    private val locationAdapter = object : RecyclerView.Adapter<LocationViewHolder>() {
        override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
            holder.binding.location = locationList[position]
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_list)
        val binding = DataBindingUtil.setContentView<ActivityLocationListBinding>(
            this,
            R.layout.activity_location_list
        )
        val locationListView = binding.locationListView
        locationListView.layoutManager = LinearLayoutManager(this)
        locationListView.adapter = locationAdapter

        viewModel.weatherList.observe(this, Observer(::onUpdateLocationList))
    }

    override fun onStart() {
        super.onStart()
        viewModel.initLocations()
    }

    private fun onUpdateLocationList(locations: List<Location>?) {
        locations ?: return
        this.locationList = locations
        locationAdapter.notifyDataSetChanged()
    }

    class LocationViewHolder(val binding: ViewLocationListitemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
