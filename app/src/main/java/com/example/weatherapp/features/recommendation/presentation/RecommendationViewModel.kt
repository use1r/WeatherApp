package com.example.weatherapp.features.recommendation.presentation

import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.ConnectivityObserver
import com.example.weatherapp.features.recommendation.domain.model.Recommendations
import com.example.weatherapp.features.recommendation.domain.repository.RecommendationRepository
import com.example.weatherapp.presentation.ViewModeWithMessageAndLoading
import com.example.weatherapp.util.Async
import com.example.weatherapp.util.Result
import com.example.weatherapp.util.UserMessage
import com.example.weatherapp.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class RecommendationUiState(
    val isLoading: Boolean = false,
    val userMessage: UserMessage? = null,
    val recommendations: Recommendations? = null,
)

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val recommendationRepository: RecommendationRepository,
    connectivityObserver: ConnectivityObserver,
) : ViewModeWithMessageAndLoading(connectivityObserver) {

    private val _recommendationsAsync = MutableStateFlow<Async<Recommendations?>>(Async.Loading)

    val uiState: StateFlow<RecommendationUiState> = combine(
        userMessage,
        isLoading,
        _recommendationsAsync,
    ) { userMessage, isLoading, recommendationsAsync ->

        when (recommendationsAsync) {
            Async.Loading -> RecommendationUiState(isLoading = true)
            is Async.Success -> {
                RecommendationUiState(
                    recommendations = recommendationsAsync.data,
                    isLoading = isLoading,
                    userMessage = userMessage,
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = RecommendationUiState(isLoading = true),
    )

    init {
        onRefresh()
    }

    override fun onRefresh() = super.runSuspend({
        val notifications = recommendationRepository.getRecommendations()
        _recommendationsAsync.value = handleResult(notifications)
    })

    private fun handleResult(result: Result<Recommendations>): Async<Recommendations?> =
        when (result) {
            is Result.Success -> Async.Success(result.data)
            is Result.Error -> {
                handleErrorResult(result)
                // If recommendation state is still Loading mean its first init and
                // has error, return null
                if (_recommendationsAsync.value is Async.Loading) {
                    Async.Success(null)
                }
                // If recommendation state already had some valid value so just keep it
                else {
                    _recommendationsAsync.value
                }
            }
        }
}
