package com.example.weatherapp.weather.fake

import com.example.weatherapp.features.weather.data.remote.dto.AllWeatherDto
import com.example.weatherapp.features.weather.domain.model.Coordinate
import com.example.weatherapp.features.weather.domain.repository.WeatherRepository
import com.example.weatherapp.util.Result
import com.example.weatherapp.weather.allWeatherDto1
import java.net.UnknownHostException

class FakeWeatherRepository : WeatherRepository {

    var haveInternet = true
    var allWeatherDto: AllWeatherDto = allWeatherDto1

    override suspend fun getAllWeather(
        coordinate: Coordinate,
        timeZone: String,
    ): Result<AllWeatherDto> {
        return if (haveInternet) {
            Result.Success(allWeatherDto)
        } else {
            Result.Error(UnknownHostException())
        }
    }
}
