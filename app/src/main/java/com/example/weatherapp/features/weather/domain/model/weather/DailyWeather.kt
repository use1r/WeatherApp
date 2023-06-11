package com.example.weatherapp.features.weather.domain.model.weather

import com.example.weatherapp.features.weather.domain.model.WeatherType
import com.example.weatherapp.util.UiText

data class DailyWeather(
    val date: UiText,
    val maxTemp: Double,
    val minTemp: Double,
    val weatherType: WeatherType,
)
