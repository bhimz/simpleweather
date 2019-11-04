package com.bhimz.simpleweather.domain.action

import com.bhimz.simpleweather.domain.model.LocationBindingModel

interface OnLocationClickListener {
    fun onClick(location: LocationBindingModel)
}