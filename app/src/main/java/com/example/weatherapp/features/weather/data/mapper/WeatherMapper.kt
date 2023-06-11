package com.example.weatherapp.features.weather.data.mapper

import com.example.weatherapp.constants.HOUR_PATTERN
import com.example.weatherapp.features.weather.data.remote.dto.AllWeatherDto
import com.example.weatherapp.features.weather.domain.model.WeatherType
import com.example.weatherapp.features.weather.domain.model.weather.CurrentWeather
import com.example.weatherapp.features.weather.domain.model.weather.DailyWeather
import com.example.weatherapp.features.weather.domain.model.weather.HourlyWeather
import com.example.weatherapp.features.weather.presentation.info.AllWeather
import com.example.weatherapp.util.countPastHoursToday
import com.example.weatherapp.util.filterPastHours
import com.example.weatherapp.util.getCurrentDate
import com.example.weatherapp.util.toDate
import com.example.weatherapp.util.toDayNameInWeek

fun AllWeatherDto.toAllWeather(timeZone: String): AllWeather {
    val count = this.hourly.time.countPastHoursToday()

    return AllWeather(
        current = this.hourly.run {
            CurrentWeather(
                date = getCurrentDate(timeZone),
                temp = temperatures[count],
                windSpeed = windSpeeds[count],
                humidity = humidities[count],
                pressure = pressures[count],
                weatherType = WeatherType.fromWMO(weatherCodes[count]),
            )
        },
        listDaily = this.daily.run {
            time.mapIndexed { index, time ->
                DailyWeather(
                    date = time.toDayNameInWeek(timeZone),
                    maxTemp = maxTemperatures[index],
                    minTemp = minTemperatures[index],
                    weatherType = WeatherType.fromWMO(weatherCodes[index]),
                )
            }
        },
        listHourly = this.hourly.run {
            time.filterPastHours().mapIndexed { index, time ->
                HourlyWeather(
                    date = time.toDate(timeZone, HOUR_PATTERN),
                    temp = temperatures[index + count],
                    windSpeed = windSpeeds[index + count],
                    weatherType = WeatherType.fromWMO(weatherCodes[index + count]),
                )
            }
        },
    )
}
