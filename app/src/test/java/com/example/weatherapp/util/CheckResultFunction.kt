package com.example.weatherapp.util

fun <T> checkResult(condition: Boolean, data: T): Result<T> {
    return if (condition) {
        Result.Success(data)
    } else {
        Result.Error(RuntimeException("Boom..."))
    }
}
