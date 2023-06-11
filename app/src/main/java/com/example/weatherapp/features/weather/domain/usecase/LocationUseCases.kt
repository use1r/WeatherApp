package com.example.weatherapp.features.weather.domain.usecase

import com.example.weatherapp.features.weather.domain.usecase.location.ValidateCurrentLocation

data class LocationUseCases(
    val validateCurrentLocation: ValidateCurrentLocation,
)
