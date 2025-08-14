package com.ozatactunahan.nativemobileapp.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozatactunahan.nativemobileapp.data.local.entity.FavoriteEntity
import com.ozatactunahan.nativemobileapp.data.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            try {
                favoriteRepository.getAllFavorites().collect { favorites ->
                    _uiState.value = _uiState.value.copy(
                        favorites = favorites,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Favoriler yüklenemedi: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun removeFromFavorites(productId: String) {
        viewModelScope.launch {
            try {
                favoriteRepository.removeFromFavorites(productId)
                loadFavorites()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Favori kaldırılamadı: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class NotificationsUiState(
    val favorites: List<FavoriteEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)