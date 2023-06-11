package com.example.weatherapp.features.weather.domain.repository

import com.example.weatherapp.features.weather.data.local.entity.LocationEntity
import com.example.weatherapp.features.weather.domain.model.Coordinate
import com.example.weatherapp.features.weather.domain.model.SuggestionCity
import com.example.weatherapp.util.Result
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    suspend fun getCurrentCoordinate(): Result<Coordinate>

    suspend fun getSuggestionCities(cityAddress: String): Result<List<SuggestionCity>>

    suspend fun checkAndUpdateCurrentLocationIfNeeded(currentCoordinate: Coordinate): Result<Boolean>

    suspend fun getLocation(coordinate: Coordinate): LocationEntity?

    suspend fun getCurrentLocation(): LocationEntity?

    fun getLocationsStream(): Flow<List<LocationEntity>>

    suspend fun saveLocation(location: LocationEntity)

    suspend fun deleteLocation(location: LocationEntity)
}
