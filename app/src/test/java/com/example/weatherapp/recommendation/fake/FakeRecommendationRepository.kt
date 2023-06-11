package com.example.weatherapp.recommendation.fake

import com.example.weatherapp.features.recommendation.domain.model.Recommendations
import com.example.weatherapp.features.recommendation.domain.repository.RecommendationRepository
import com.example.weatherapp.recommendation.recommendations1
import com.example.weatherapp.util.Result

class FakeRecommendationRepository : RecommendationRepository {

    var isSuccessful: Boolean = true
    var recommendations: Recommendations? = null
    var exception: Exception = RuntimeException("BOOM!!")

    override suspend fun getRecommendations(): Result<Recommendations> {
        return if (isSuccessful) {
            Result.Success(recommendations ?: recommendations1)
        } else {
            Result.Error(exception)
        }
    }
}
