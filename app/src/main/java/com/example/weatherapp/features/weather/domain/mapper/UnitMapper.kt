package com.example.weatherapp.features.weather.domain.mapper

import com.example.weatherapp.features.weather.domain.model.unit.LabeledEnum
import com.example.weatherapp.features.weather.domain.model.unit.PressureUnit
import com.example.weatherapp.features.weather.domain.model.unit.TemperatureUnit
import com.example.weatherapp.features.weather.domain.model.unit.TimeFormatUnit
import com.example.weatherapp.features.weather.domain.model.unit.WindSpeedUnit

fun getTemperatureUnit(label: String) = getUnit<TemperatureUnit>(label)

fun getWindSpeedUnit(label: String) = getUnit<WindSpeedUnit>(label)

fun getPressureUnit(label: String) = getUnit<PressureUnit>(label)

fun getTimeFormatUnit(label: String) = getUnit<TimeFormatUnit>(label)

inline fun <reified T> getUnit(label: String): T where T : Enum<T>, T : LabeledEnum {
    for (unit in enumValues<T>()) {
        if (label == unit.label) return unit
    }

    return enumValueOf("NULL")
}
