package com.example.weatherapp.presentation

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.constants.DELAY_TIME
import com.example.weatherapp.domain.ConnectivityObserver
import com.example.weatherapp.util.Result
import com.example.weatherapp.util.UiText
import com.example.weatherapp.util.UserMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.UnknownHostException

abstract class ViewModeWithMessageAndLoading(
    private val connectivityObserver: ConnectivityObserver,
) : ViewModel() {

    protected val userMessage: MutableStateFlow<UserMessage?> = MutableStateFlow(null)
    protected val isLoading = MutableStateFlow(false)
    private var refreshJob: Job? = null

    abstract fun onRefresh()

    fun onHandleUserMessageDone() {
        userMessage.value = null
    }

    protected fun showSnackbarMessage(
        @StringRes messageResId: Int,
        @StringRes actionLabel: Int? = null,
        vararg arg: Any,
    ) {
        userMessage.value =
            UserMessage(UiText.StringResource(messageResId, arg.toList()), actionLabel)
    }

    protected open fun runSuspend(vararg functions: suspend () -> Unit) {
        isLoading.value = true
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            val job = launch {
                functions.forEach { launch { it() } }
                launch { delay(DELAY_TIME) }
            }

            job.join()
            isLoading.value = false
        }
    }

    protected fun handleErrorResult(
        result: Result.Error,
        shouldShowErrorMessage: Boolean = true,
    ) {
        Log.d(this.javaClass.simpleName, result.toString())
        val messageId = when (result.exception) {
            is UnknownHostException -> {
                refreshJob?.cancel()
                isLoading.value = false
                viewModelScope.launch {
                    connectivityObserver.observe()
                        .first { it == ConnectivityObserver.Status.Available }
                        .let {
                            onRefresh()
                            showSnackbarMessage(R.string.restore_internet_connection)
                        }
                }
                R.string.no_internet_connection
            }

            else -> R.string.please_enter_location
        }
        if (shouldShowErrorMessage) showSnackbarMessage(messageId)
    }
}
