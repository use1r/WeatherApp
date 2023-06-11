package com.example.weatherapp.fake

import com.example.weatherapp.domain.ConnectivityObserver
import com.example.weatherapp.domain.ConnectivityObserver.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

class FakeConnectivityObserver : ConnectivityObserver {
    private val statusFlow = MutableStateFlow<Status?>(null)

    fun setStatus(status: Status) {
        statusFlow.value = status
    }

    override fun observe(): Flow<Status> {
        return statusFlow.filterNotNull()
    }
}
