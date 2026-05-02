package com.example.anganwadiapp.core.common

interface Result<out T> {
    data class Success<out T>(val data: T) : Result<T>
    data class Error(val message: String, val exception: Throwable? = null) : Result<Nothing>
    object Loading : Result<Nothing>
}
