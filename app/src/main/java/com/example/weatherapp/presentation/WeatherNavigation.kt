package com.example.weatherapp.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.weatherapp.features.weather.domain.model.Coordinate
import com.example.weatherapp.presentation.WeatherDestinationsArgs.COUNTRY_CODE_ARG
import com.example.weatherapp.presentation.WeatherDestinationsArgs.LATITUDE_ARG
import com.example.weatherapp.presentation.WeatherDestinationsArgs.LONGITUDE_ARG
import com.example.weatherapp.presentation.WeatherDestinationsArgs.NAVIGATION_KEY_ARG
import com.example.weatherapp.presentation.WeatherScreens.INFO_SCREEN
import com.example.weatherapp.presentation.WeatherScreens.NOTIFICATION_SCREEN
import com.example.weatherapp.presentation.WeatherScreens.SEARCH_SCREEN

object WeatherScreens {
    const val INFO_SCREEN = "infoScreen"
    const val SEARCH_SCREEN = "searchScreen"
    const val SETTING_SCREEN = "settingScreen"
    const val NOTIFICATION_SCREEN = "notificationScreen"
}

object WeatherDestinationsArgs {
    const val LATITUDE_ARG = "latitude"
    const val LONGITUDE_ARG = "longitude"
    const val NAVIGATION_KEY_ARG = "navigationKey"
    const val COUNTRY_CODE_ARG = "countryCode"
}

object WeatherDestinations {
    const val INFO_ROUTE =
        "$INFO_SCREEN?$COUNTRY_CODE_ARG={$COUNTRY_CODE_ARG}" +
            "&$LATITUDE_ARG={$LATITUDE_ARG}" +
            "&$LONGITUDE_ARG={$LONGITUDE_ARG}" +
            "&$NAVIGATION_KEY_ARG={$NAVIGATION_KEY_ARG}"

    const val SEARCH_ROUTE = SEARCH_SCREEN
    const val NOTIFICATION_ROUTE = NOTIFICATION_SCREEN
}

class WeatherNavigationActions(private val navController: NavController) {

    fun navigateToInfo(
        coordinate: Coordinate,
        countryCode: String,
        navigationKey: Int = 0,
    ) {
        navController.navigate(
            INFO_SCREEN.let {
                "$it?$COUNTRY_CODE_ARG=$countryCode" +
                    "&$LATITUDE_ARG=${coordinate.latitude}" +
                    "&$LONGITUDE_ARG=${coordinate.longitude}" +
                    "&$NAVIGATION_KEY_ARG=$navigationKey"
            },
        ) {
            popUpTo(navController.graph.findStartDestination().id)
        }
    }

    fun navigateToSearch() {
        navController.navigate(WeatherDestinations.SEARCH_ROUTE)
    }

    fun navigateToNotification() {
        navController.navigate(WeatherDestinations.NOTIFICATION_ROUTE)
    }
}
