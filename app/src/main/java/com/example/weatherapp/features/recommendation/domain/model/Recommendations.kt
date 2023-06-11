package com.example.weatherapp.features.recommendation.domain.model

data class Recommendations(
    val uvRecommendation: UvRecommendation? = null,
    val aqiRecommendation: AqiRecommendation? = null,
)
