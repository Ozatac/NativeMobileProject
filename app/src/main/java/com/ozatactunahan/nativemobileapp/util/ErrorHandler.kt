package com.ozatactunahan.nativemobileapp.util

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
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

        val errorState = ErrorMapper.mapToErrorState(
            message,
            networkUtils
        )

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
}
