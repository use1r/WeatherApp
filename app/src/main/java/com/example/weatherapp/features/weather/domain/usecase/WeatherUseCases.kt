package com.example.weatherapp.features.weather.domain.usecase

import com.example.weatherapp.features.weather.domain.usecase.weather.ConvertUnit
import com.example.weatherapp.features.weather.domain.usecase.weather.GetAllWeather

data class WeatherUseCases(
    val getAllWeather: GetAllWeather,
    val convertUnit: ConvertUnit,
)
