package com.example.weatherapp.features.weather.data.di

import android.content.Context
import com.example.weatherapp.di.DefaultDispatcher
import com.example.weatherapp.features.weather.data.local.AppDatabase
import com.example.weatherapp.features.weather.data.remote.WeatherApi
import com.example.weatherapp.features.weather.data.repository.DefaultLocationRepository
import com.example.weatherapp.features.weather.data.repository.DefaultWeatherRepository
import com.example.weatherapp.features.weather.domain.repository.LocationRepository
import com.example.weatherapp.features.weather.domain.repository.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherRepositoryModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(api: WeatherApi): WeatherRepository =
        DefaultWeatherRepository(api)

    @Provides
    @Singleton
    fun provideLocationRepository(
        api: WeatherApi,
        db: AppDatabase,
        client: FusedLocationProviderClient,
        @ApplicationContext context: Context,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
    ): LocationRepository = DefaultLocationRepository(
        dao = db.locationDao(),
        api = api,
        client = client,
        context = context,
        defaultDispatcher = defaultDispatcher,
    )
}
