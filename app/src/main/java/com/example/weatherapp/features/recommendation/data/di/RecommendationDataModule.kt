package com.example.weatherapp.features.recommendation.data.di

import com.example.weatherapp.domain.AppPreferences
import com.example.weatherapp.features.recommendation.data.remote.RecommendationApi
import com.example.weatherapp.features.recommendation.data.repository.DefaultRecommendationRepository
import com.example.weatherapp.features.recommendation.domain.repository.RecommendationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RecommendationDataModule {

    @Provides
    @Singleton
    fun provideRecommendationApi(retrofit: Retrofit): RecommendationApi =
        retrofit.create(RecommendationApi::class.java)

    @Provides
    @Singleton
    fun provideRecommendationRepository(
        api: RecommendationApi,
        preferences: AppPreferences,
    ): RecommendationRepository =
        DefaultRecommendationRepository(api, preferences)
}
