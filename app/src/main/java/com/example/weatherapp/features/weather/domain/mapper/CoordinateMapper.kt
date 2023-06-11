package com.example.weatherapp.features.weather.domain.mapper

import com.example.weatherapp.features.weather.domain.model.Coordinate
import com.example.weatherapp.locationpreferences.LocationPreferences
import com.example.weatherapp.util.roundTo

val LocationPreferences.coordinate: Coordinate
    get() = Coordinate(this.latitude, this.longitude)

fun Coordinate.roundTo(n: Int) = Coordinate(latitude.roundTo(n), longitude.roundTo(n))
