package com.ozatactunahan.nativemobileapp.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<NotificationsUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        loadNotifications()
    }

    fun onUiEvent(event: NotificationsUiEvent) {
        when (event) {
            is NotificationsUiEvent.Refresh -> {
                loadNotifications()
            }
            is NotificationsUiEvent.ClearAll -> {
                clearAllNotifications()
            }
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Simulate loading notifications
            kotlinx.coroutines.delay(1000)
            
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    notifications = listOf(
                        "Yeni ürün eklendi: iPhone 15",
                        "Siparişiniz kargoya verildi",
                        "Favori ürününüz indirime girdi"
                    )
                )
            }
        }
    }

    private fun clearAllNotifications() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(notifications = emptyList()) }
                _uiEffect.emit(NotificationsUiEffect.AllNotificationsCleared)
            } catch (e: Exception) {
                _uiEffect.emit(NotificationsUiEffect.ShowError("Bildirimler temizlenemedi: ${e.message}"))
            }
        }
    }
}

data class NotificationsUiState(
    val notifications: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) 
