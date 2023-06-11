package com.example.weatherapp.features.weather.presentation.search

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.constants.DELAY_TIME
import com.example.weatherapp.domain.AppPreferences
import com.example.weatherapp.domain.ConnectivityObserver
import com.example.weatherapp.features.weather.data.local.entity.LocationEntity
import com.example.weatherapp.features.weather.data.mapper.coordinate
import com.example.weatherapp.features.weather.data.mapper.toSavedCity
import com.example.weatherapp.features.weather.domain.model.CityUiModel
import com.example.weatherapp.features.weather.domain.model.SavedCity
import com.example.weatherapp.features.weather.domain.model.SuggestionCity
import com.example.weatherapp.features.weather.domain.model.WeatherType
import com.example.weatherapp.features.weather.domain.repository.LocationRepository
import com.example.weatherapp.features.weather.domain.usecase.LocationUseCases
import com.example.weatherapp.features.weather.domain.usecase.WeatherUseCases
import com.example.weatherapp.presentation.ViewModeWithMessageAndLoading
import com.example.weatherapp.util.Async
import com.example.weatherapp.util.LocationException
import com.example.weatherapp.util.LocationException.LocationPermissionDeniedException
import com.example.weatherapp.util.LocationException.LocationServiceDisabledException
import com.example.weatherapp.util.Result
import com.example.weatherapp.util.UserMessage
import com.example.weatherapp.util.UserMessage.RequestingLocationPermission
import com.example.weatherapp.util.UserMessage.RequestingTurnOnLocationService
import com.example.weatherapp.util.isNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WeatherSearchViewModel"
private const val REQUIRED_INPUT_LENGTH = 3

@HiltViewModel
class WeatherSearchViewModel @Inject constructor(
    private val locationUseCases: LocationUseCases,
    private val weatherUseCases: WeatherUseCases,
    private val locationRepository: LocationRepository,
    connectivityObserver: ConnectivityObserver,
    private val appPreferences: AppPreferences,
) : ViewModeWithMessageAndLoading(connectivityObserver) {

    private val _temperatureUnit = appPreferences.temperatureUnitFlow
        .map {
            it.also { Log.d(TAG, "TemperatureUnit flow collected: $it") }
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null,
        )


    private val _locations = locationRepository.getLocationsStream().map {
        it.also { Log.d(TAG, "Locations flow collected: $it") }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList(),
    )

    private val _input = MutableStateFlow("")
    private val _savedCitiesAsync = MutableStateFlow<Async<List<SavedCity>>>(Async.Loading)
    private val _suggestionCities = MutableStateFlow<List<SuggestionCity>>(emptyList())

    private val _viewModelState = combine(
        _input,
        _savedCitiesAsync,
        _suggestionCities,
        isLoading,
        userMessage,
    ) { input, savedCitiesAsync, suggestionCities, isLoading, userMessage ->

        when (savedCitiesAsync) {
            Async.Loading -> WeatherSearchViewModelState(isLoading = true)
            is Async.Success -> WeatherSearchViewModelState(
                input = input,
                isLoading = isLoading,
                userMessage = userMessage,
                suggestionCities = suggestionCities,
                savedCities = weatherUseCases.convertUnit(
                    savedCitiesAsync.data, _temperatureUnit.value
                ),
            )
        }
    }

    private val _uiState =
        MutableStateFlow(WeatherSearchViewModelState(isLoading = true).toUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val validateResult = locationUseCases.validateCurrentLocation()
            if (validateResult is Result.Error) {
                when (validateResult.exception) {
                    is LocationException -> locationRepository.apply {
                        getCurrentLocation()?.let { deleteLocation(it) }
                    }

                    else -> {
                        _savedCitiesAsync.value = Async.Success(emptyList())
                        handleErrorResult(validateResult)
                    }
                }
            }

            launch {
                _viewModelState.collect { _uiState.value = it.toUiState() }
            }

            onRefresh()
        }
    }

    private lateinit var tempSavedCity: SavedCity

    fun onInputUpdate(input: String) {
        _input.value = input
        refreshSuggestionCities()
    }

    override fun onRefresh() = runSuspend({
        refreshSuggestionCities()
        if (_locations.value.isEmpty()) return@runSuspend

        val channel = Channel<Result<SavedCity>>()
        handleLocations(channel, _locations.value)

        // Use ArrayDeque to add current location to the beginning of the array later
        val savedCities = ArrayDeque<SavedCity>()
        var currentCity: SavedCity? = null

        run repeatBlock@{
            repeat(_locations.value.size) {
                when (val city = channel.receive()) {
                    is Result.Success -> {
                        if (city.data.isCurrentLocation) currentCity = city.data
                        else savedCities.add(city.data)
                    }

                    is Result.Error -> {
                        _savedCitiesAsync.value = Async.Success(emptyList())
                        handleErrorResult(city)
                        return@repeatBlock
                    }
                }
            }
        }

        // Since we are using a channel to concurrently request APIs for all locations,
        // it is necessary to sort the city list in the order they were added.
        savedCities.sortBy { it.id }
        // Add the current city to the beginning of the city list if it isn't null
        _savedCitiesAsync.value =
            Async.Success(if (currentCity.isNull()) savedCities else savedCities.apply {
                addFirst(currentCity!!)
            })

        Log.d(
            TAG, "SavedCities flow collected: $savedCities"
        )
    })

    fun onDeleteLocation() {
        viewModelScope.launch {
            locationRepository.apply {
                deleteLocation(getLocation(tempSavedCity.coordinate)!!)
            }

            _savedCitiesAsync.update {
                Async.Success((it as Async.Success).data.toMutableList()
                    .apply { remove(tempSavedCity) })
            }

            showSnackbarMessage(R.string.location_deleted)
        }
    }

    fun onSavedCityLongClick(city: SavedCity) {
        if (city.isCurrentLocation) return
        tempSavedCity = city
        userMessage.value = UserMessage.DeletingLocation(city.cityAddress)
    }

    fun onLocationFieldClick() {
        viewModelScope.launch {
            when (val result = locationUseCases.validateCurrentLocation()) {
                is Result.Success -> onRefresh()
                is Result.Error -> {
                    Log.d(TAG, result.exception.toString())
                    when (result.exception) {
                        is LocationPermissionDeniedException -> userMessage.value =
                            RequestingLocationPermission

                        is LocationServiceDisabledException -> userMessage.value =
                            RequestingTurnOnLocationService
                    }
                }
            }
        }
    }

    fun onPermissionActionResult(isGranted: Boolean, shouldDelay: Boolean = false) {
        if (!isGranted) return

        viewModelScope.launch {
            isLoading.value = true
            // Delay to wait for the location service turn on
            if (shouldDelay) delay(DELAY_TIME)
            locationUseCases.validateCurrentLocation()
            onRefresh()
        }
    }

    fun onItemClick(city: CityUiModel, onItemClick: (CityUiModel) -> Unit) = viewModelScope.launch {
        when (city) {
            is SavedCity -> {
                appPreferences.updateLocation(
                    cityAddress = city.cityAddress,
                    coordinate = city.coordinate,
                    timeZone = city.timeZone,
                    isCurrentLocation = city.isCurrentLocation,
                )
            }

            is SuggestionCity -> {
                appPreferences.updateLocation(
                    cityAddress = city.cityAddress,
                    coordinate = city.coordinate,
                    timeZone = city.timeZone,
                    isCurrentLocation = null,
                )
            }
        }

        onItemClick(city)
    }

    private fun handleLocations(
        channel: Channel<Result<SavedCity>>, locations: List<LocationEntity>
    ) {
        locations.forEach { location ->
            viewModelScope.launch {
                channel.send(when (val weather =
                    weatherUseCases.getAllWeather(location.coordinate, location.timeZone)) {

                    is Result.Error -> weather
                    is Result.Success -> {
                        val city = weather.data.hourly.let {
                            location.toSavedCity(
                                it.temperatures[0],
                                WeatherType.fromWMO(it.weatherCodes[0]),
                            )
                        }

                        Result.Success(city)
                    }
                })
            }
        }
    }

    private fun handleSuggestionCitiesResult(result: Result<List<SuggestionCity>>): List<SuggestionCity> {
        return when (result) {
            is Result.Success -> result.data
            is Result.Error -> {
                handleErrorResult(result, false)
                emptyList()
            }
        }
    }

    private fun refreshSuggestionCities() {
        viewModelScope.launch {
            _suggestionCities.value = _input.value.let {
                if (it.length < REQUIRED_INPUT_LENGTH) {
                    emptyList()
                } else {
                    handleSuggestionCitiesResult(locationRepository.getSuggestionCities(it))
                }
            }
        }
    }
}
