package com.example.weatherapp.features.weather.presentation.info

import com.example.weatherapp.features.weather.domain.model.unit.AllUnit
import com.example.weatherapp.features.weather.domain.model.weather.CurrentWeather
import com.example.weatherapp.features.weather.domain.model.weather.DailyWeather
import com.example.weatherapp.features.weather.domain.model.weather.HourlyWeather
import com.example.weatherapp.util.UserMessage

data class WeatherInfoUiState(
    val isLoading: Boolean = false,
    val userMessage: UserMessage? = null,
    val isCurrentLocation: Boolean = false,
    val cityAddress: String = "",
    val allUnit: AllUnit? = null,
    val allWeather: AllWeather = AllWeather(),
)

data class AllWeather(
    val current: CurrentWeather? = null,
    val listDaily: List<DailyWeather> = emptyList(),
    val listHourly: List<HourlyWeather> = emptyList(),
)
