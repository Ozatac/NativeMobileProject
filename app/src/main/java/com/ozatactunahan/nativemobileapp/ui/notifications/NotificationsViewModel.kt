package com.ozatactunahan.nativemobileapp.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozatactunahan.nativemobileapp.data.local.entity.FavoriteEntity
import com.ozatactunahan.nativemobileapp.domain.repository.FavoriteRepository
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
class NotificationsViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<NotificationsUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        loadFavorites()
    }

    fun onUiEvent(event: NotificationsUiEvent) {
        when (event) {
            is NotificationsUiEvent.Refresh -> {
                loadFavorites()
            }
            is NotificationsUiEvent.RemoveFromFavorites -> {
                removeFromFavorites(event.favoriteEntity)
            }
            is NotificationsUiEvent.ClearAll -> {
                clearAllFavorites()
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                favoriteRepository.getAllFavorites().collect { favorites ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            favorites = favorites,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load favorites: ${e.message}"
                    )
                }
            }
        }
    }

    private fun removeFromFavorites(favoriteEntity: FavoriteEntity) {
        viewModelScope.launch {
            try {
                favoriteRepository.removeFromFavorites(favoriteEntity.productId)
                _uiEffect.emit(NotificationsUiEffect.ShowToast("Removed from favorites"))
            } catch (e: Exception) {
                _uiEffect.emit(NotificationsUiEffect.ShowError("Failed to remove from favorites: ${e.message}"))
            }
        }
    }

    private fun clearAllFavorites() {
        viewModelScope.launch {
            try {
                // Tüm favorileri kaldır
                val currentFavorites = _uiState.value.favorites
                currentFavorites.forEach { favorite ->
                    favoriteRepository.removeFromFavorites(favorite.productId)
                }
                
                _uiState.update { it.copy(favorites = emptyList()) }
                _uiEffect.emit(NotificationsUiEffect.AllFavoritesCleared)
            } catch (e: Exception) {
                _uiEffect.emit(NotificationsUiEffect.ShowError("Failed to clear all favorites: ${e.message}"))
            }
        }
    }
}

data class NotificationsUiState(
    val favorites: List<FavoriteEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) 
