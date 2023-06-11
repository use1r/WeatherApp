package com.example.weatherapp.presentation

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.features.weather.presentation.info.WeatherInfoViewModel
import com.example.weatherapp.presentation.theme.WeatherTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: WeatherInfoViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) {
            viewModel.onFirstTimeLocationPermissionResult(it.containsValue(true))
        }

        installSplashScreen().setKeepOnScreenCondition {
            viewModel.isInitializing.value
        }

        lifecycleScope.launch {
            val isFirstTimeRunApp = viewModel.isFirstTimeRunApp()
            if (isFirstTimeRunApp) {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    ),
                )
            } else {
                viewModel.onRefresh()
            }
        }

        lifecycleScope.launch {
            viewModel.isInitializing.flowWithLifecycle(lifecycle).first { !it }.let {
                setContent {
                    WeatherTheme {
                        WeatherNavGraph(startDestination = viewModel.appRoute)
                    }
                }
            }
        }

        setContent {
            WeatherTheme {
                WeatherNavGraph()
            }
        }
    }
}
