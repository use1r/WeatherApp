package com.example.weatherapp.features.weather.data.repository

import com.example.weatherapp.features.weather.data.remote.WeatherApi
import com.example.weatherapp.features.weather.data.remote.dto.AllWeatherDto
import com.example.weatherapp.features.weather.domain.model.Coordinate
import com.example.weatherapp.features.weather.domain.repository.WeatherRepository
import com.example.weatherapp.util.Result
import com.example.weatherapp.util.runCatching

class DefaultWeatherRepository(
    private val api: WeatherApi,
) : WeatherRepository {

    override suspend fun getAllWeather(
        coordinate: Coordinate,
        timeZone: String,
    ): Result<AllWeatherDto> = runCatching {
        api.getAllWeather(
            lat = coordinate.latitude,
            long = coordinate.longitude,
            timeZone = timeZone,
        )
    }
}
