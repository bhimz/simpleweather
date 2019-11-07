package com.bhimz.simpleweather

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.bhimz.simpleweather.domain.model.Location


class WeatherFragment : Fragment() {
    private val permissionRequestCode = 1001
    private val placeAutoCompleteRequestCode = 1002

    private val viewModel: WeatherMainViewModel by activityViewModels()

    private var listItems: List<ListItemModel> = listOf()

    private val locationAdapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is LocationViewHolder -> {
                    val item = listItems[position]
                    holder.binding.location = item.itemData()
                    holder.binding.setOnLocationClickListener {
                        val action = WeatherFragmentDirections.actionOpenWeatherDetail(item.actualIndex)
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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
        val fabAdd = binding.fabAdd
        fabAdd.setOnClickListener {
            val context = context ?: return@setOnClickListener
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            ).build(context)
            startActivityForResult(intent, placeAutoCompleteRequestCode)
        }

        viewModel.locationList.observe(viewLifecycleOwner, Observer(::onUpdateLocationList))
        viewModel.uiState.observe(viewLifecycleOwner, Observer(::onUiStateChanged))
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            placeAutoCompleteRequestCode -> if (resultCode == Activity.RESULT_OK && data != null) {
                val place = Autocomplete.getPlaceFromIntent(data)
                val placeName = place.name
                val latLng = place.latLng
                if (placeName != null && latLng != null) {
                    viewModel.addNewLocation(Location(placeName, latLng.latitude, latLng.longitude))
                }
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
            viewModel.initCurrentLocation()
        }
    }

    private fun onUiStateChanged(uiState: UiState?) {
        uiState ?: return
        when(uiState) {
            is InitialState -> {
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
                        viewModel.initCurrentLocation()
                    }
                }
            }
            is LocationServiceUnavailableState -> context?.let {
                Toast.makeText(it, "Location service is unavailable", Toast.LENGTH_SHORT).show()
            }
            is WeatherServiceUnavailableState -> context?.let {
                Toast.makeText(it, "Weather service cannot be reached", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onUpdateLocationList(locations: List<LocationBindingModel>?) {
        locations ?: return
        val items = mutableListOf(ListItemModel(-1, HEADER_VIEW, "Current Location"))
        locations.forEachIndexed { index, model ->
            if (index == 1) {
                items.add(ListItemModel(-1, HEADER_VIEW, "Saved Locations"))
            }
            items.add(ListItemModel(index, ITEM_VIEW, model))
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
