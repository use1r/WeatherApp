package com.example.weatherapp.features.weather.domain.mapper

import com.example.weatherapp.features.weather.presentation.info.AllWeather

fun AllWeather.convertTemperatureUnit(convertMethod: (Double) -> Double) = this.copy(
    current = current?.copy(
        temp = convertMethod(current.temp),
    ),
    listDaily = listDaily.map {
        it.copy(
            maxTemp = convertMethod(it.maxTemp),
            minTemp = convertMethod(it.minTemp),
        )
    },
    listHourly = listHourly.map {
        it.copy(
            temp = convertMethod(it.temp),
        )
    },
)

fun AllWeather.convertWindSpeedUnit(convertMethod: (Double) -> Double) = this.copy(
    current = current?.copy(
        windSpeed = convertMethod(current.windSpeed),
    ),
    listHourly = listHourly.map {
        it.copy(
            windSpeed = convertMethod(it.windSpeed),
        )
    },
)

fun AllWeather.convertPressureUnit(convertMethod: (Double) -> Double) = this.copy(
    current = current?.copy(
        pressure = convertMethod(current.pressure),
    ),
)

fun AllWeather.convertTimeFormatUnit(convertMethod: (String) -> String) = this.copy(
    current = current?.copy(
        date = convertMethod(current.date),
    ),
)
