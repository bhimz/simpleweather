package com.bhimz.simpleweather.domain.action

import com.bhimz.simpleweather.domain.model.Location

interface OnLocationClickListener {
    fun onClick(location: Location)
}