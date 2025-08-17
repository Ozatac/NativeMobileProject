package com.ozatactunahan.nativemobileapp.ui.base

import com.ozatactunahan.nativemobileapp.util.NetworkUtils

data class ErrorState(
    val message: String? = null,
    val errorType: ErrorType = ErrorType.NONE,
    val showRetry: Boolean = true
) {
    fun getDisplayMessage(networkUtils: NetworkUtils): String {
        if (message.isNullOrBlank()) return ""
        
        return when (errorType) {
            ErrorType.NETWORK -> "Please check your internet connection and try again"
            ErrorType.TIMEOUT -> "Connection timed out. Please try again"
            ErrorType.SERVER_ERROR -> "Server error. Please try again later"
            ErrorType.NOT_FOUND -> "Content not found. Please try again"
            ErrorType.UNKNOWN -> message
            ErrorType.NONE -> ""
        }
    }
}

enum class ErrorType {
    NONE,
    NETWORK,
    TIMEOUT,
    SERVER_ERROR,
    NOT_FOUND,
    UNKNOWN
}

object ErrorMapper {
    fun mapToErrorState(message: String?, networkUtils: NetworkUtils): ErrorState {
        if (message.isNullOrBlank()) return ErrorState()
        
        return when {
            !networkUtils.isNetworkAvailable() -> {
                ErrorState(message, ErrorType.NETWORK)
            }
            message.contains("timeout", ignoreCase = true) -> {
                ErrorState(message, ErrorType.TIMEOUT)
            }
            message.contains("500", ignoreCase = true) -> {
                ErrorState(message, ErrorType.SERVER_ERROR)
            }
            message.contains("404", ignoreCase = true) -> {
                ErrorState(message, ErrorType.NOT_FOUND)
            }
            message.contains("network", ignoreCase = true) -> {
                ErrorState(message, ErrorType.NETWORK)
            }
            else -> {
                ErrorState(message, ErrorType.UNKNOWN)
            }
        }
    }
}
