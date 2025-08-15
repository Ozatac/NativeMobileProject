package com.ozatactunahan.nativemobileapp.util

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.ozatactunahan.nativemobileapp.ui.base.ErrorState
import com.ozatactunahan.nativemobileapp.ui.base.ErrorType
import com.ozatactunahan.nativemobileapp.ui.base.ErrorMapper

object ErrorHandler {
    
    fun showError(
        context: Context,
        errorLayout: View?,
        errorText: TextView?,
        retryButton: Button?,
        message: String?,
        networkUtils: NetworkUtils,
        onRetry: () -> Unit
    ) {
        if (message.isNullOrBlank()) {
            hideError(errorLayout)
            return
        }
        
        val errorState = ErrorMapper.mapToErrorState(message, networkUtils)
        
        errorText?.text = errorState.getDisplayMessage(networkUtils)
        errorLayout?.visibility = View.VISIBLE
        
        retryButton?.setOnClickListener {
            onRetry()
            hideError(errorLayout)
        }
    }
    
    fun hideError(errorLayout: View?) {
        errorLayout?.visibility = View.GONE
    }
    
    fun getErrorMessage(message: String, networkUtils: NetworkUtils): String {
        return when {
            !networkUtils.isNetworkAvailable() -> {
                "Please check your internet connection and try again"
            }
            message.contains("timeout", ignoreCase = true) -> {
                "Connection timed out. Please try again"
            }
            message.contains("500", ignoreCase = true) -> {
                "Server error. Please try again later"
            }
            message.contains("404", ignoreCase = true) -> {
                "Content not found. Please try again"
            }
            message.contains("network", ignoreCase = true) -> {
                "Network error. Please check your internet connection"
            }
            else -> {
                message
            }
        }
    }
}
