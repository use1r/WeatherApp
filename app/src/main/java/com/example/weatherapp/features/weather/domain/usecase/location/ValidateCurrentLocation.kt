package com.example.weatherapp.features.weather.domain.usecase.location

import com.example.weatherapp.domain.AppPreferences
import com.example.weatherapp.features.weather.data.mapper.coordinate
import com.example.weatherapp.features.weather.domain.mapper.coordinate
import com.example.weatherapp.features.weather.domain.mapper.roundTo
import com.example.weatherapp.features.weather.domain.repository.LocationRepository
import com.example.weatherapp.locationpreferences.LocationPreferences
import com.example.weatherapp.util.Result
import com.example.weatherapp.util.isNull

@Suppress("ReturnCount")
class ValidateCurrentLocation(
    private val repository: LocationRepository,
    private val preferences: AppPreferences,
) {

    suspend operator fun invoke(): Result<Boolean> {
        val currentCoordinate = when (val result = repository.getCurrentCoordinate()) {
            is Result.Success -> result.data
            is Result.Error -> return result
        }

        val currentLocation = repository.getCurrentLocation()
        // If current location in db is null, fetch it with currentCoordinate
            ?: return when (
                val result =
                    repository.checkAndUpdateCurrentLocationIfNeeded(currentCoordinate)
            ) {
                is Result.Success -> Result.Success(true)
                is Result.Error -> result
            }

        if (currentLocation.coordinate != currentCoordinate) {
            repository.deleteLocation(currentLocation)
            return when (
                val result =
                    repository.checkAndUpdateCurrentLocationIfNeeded(currentCoordinate)
            ) {
                is Result.Success -> Result.Success(true)
                is Result.Error -> result
            }
        }

        return Result.Success(true)
    }

    suspend operator fun invoke(
        location: LocationPreferences,
    ): Result<Boolean> {
        // Passed location is valid
        if (location.hasIsCurrentLocation()) return Result.Success(true)
        // Passed location is valid
        if (location != LocationPreferences.getDefaultInstance()) {
            val currentCoordinate = repository.getCurrentLocation()?.coordinate
            // If current location in db is null return false cuz couldn't compare with passed one
            if (currentCoordinate.isNull()) {
                preferences.updateIsCurrentLocation(false)
                return Result.Success(true)
            }

            if (currentCoordinate!!.roundTo(1) == location.coordinate.roundTo(1)) {
                preferences.updateIsCurrentLocation(true)
                return Result.Success(true)
            }
        }

        val currentCoordinate = when (val result = repository.getCurrentCoordinate()) {
            is Result.Success -> result.data
            is Result.Error -> return result
        }

        return when (val isCheckSuccess =
            repository.checkAndUpdateCurrentLocationIfNeeded(currentCoordinate)) {


            is Result.Success -> {
                repository.getCurrentLocation()?.let {
                    preferences.updateLocation(it.cityAddress, it.coordinate, it.timeZone, true)
                }

                Result.Success(false)
            }

            is Result.Error -> isCheckSuccess
        }
    }
}
