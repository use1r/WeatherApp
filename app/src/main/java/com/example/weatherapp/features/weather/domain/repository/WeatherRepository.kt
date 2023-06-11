package com.example.weatherapp.features.weather.domain.repository

import com.example.weatherapp.features.weather.data.remote.dto.AllWeatherDto
import com.example.weatherapp.features.weather.domain.model.Coordinate
import com.example.weatherapp.util.Result

interface WeatherRepository {

    suspend fun getAllWeather(
        coordinate: Coordinate,
        timeZone: String,
    ): Result<AllWeatherDto>
}
