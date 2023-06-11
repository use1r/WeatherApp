package com.example.weatherapp.features.recommendation.domain.repository

import com.example.weatherapp.features.recommendation.domain.model.Recommendations
import com.example.weatherapp.util.Result

interface RecommendationRepository {

    suspend fun getRecommendations(): Result<Recommendations>
}
