package com.example.weatherapp.features.weather.presentation.search

import com.example.weatherapp.features.weather.domain.model.SavedCity
import com.example.weatherapp.features.weather.domain.model.SuggestionCity
import com.example.weatherapp.util.UserMessage

sealed interface WeatherSearchState {
    val input: String
    val isLoading: Boolean
    val userMessage: UserMessage?

    data class ShowSaveCities(
        val savedCities: List<SavedCity>,
        override val input: String,
        override val isLoading: Boolean,
        override val userMessage: UserMessage?,
    ) : WeatherSearchState

    data class ShowSuggestionCities(
        val suggestionCities: List<SuggestionCity>,
        override val input: String,
        override val isLoading: Boolean,
        override val userMessage: UserMessage?,
    ) : WeatherSearchState
}

data class WeatherSearchViewModelState(
    val input: String = "",
    val isLoading: Boolean = false,
    val userMessage: UserMessage? = null,
    val savedCities: List<SavedCity> = emptyList(),
    val suggestionCities: List<SuggestionCity> = emptyList(),
) {
    fun toUiState(): WeatherSearchState =
        if (input.isBlank()) {
            WeatherSearchState.ShowSaveCities(
                input = input,
                isLoading = isLoading,
                userMessage = userMessage,
                savedCities = savedCities,
            )
        } else {
            WeatherSearchState.ShowSuggestionCities(
                input = input,
                isLoading = isLoading,
                userMessage = userMessage,
                suggestionCities = suggestionCities,
            )
        }
}
