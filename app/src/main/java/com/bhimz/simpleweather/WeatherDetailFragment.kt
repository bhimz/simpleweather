package com.bhimz.simpleweather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs

class WeatherDetailFragment : Fragment() {

    private val args: WeatherDetailFragmentArgs by navArgs()
    private val locationName by lazy { args.locationName }
    private val latitude by lazy { args.latitude.toDouble() }
    private val longitude by lazy { args.longitude.toDouble() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather_detail, container, false)
    }
}
