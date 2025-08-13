package com.ozatactunahan.nativemobileapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.data.repository.FavoriteRepository
import com.ozatactunahan.nativemobileapp.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
        loadFavoriteStates()
    }

    fun loadProducts(searchQuery: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getProductsUseCase(viewModelScope, searchQuery) // Query parametresi eklendi
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Bir hata oluştu"
                        )
                    }
                }
                .collect { pagingData ->
                    _uiState.update {
                        it.copy(
                            pagingData = pagingData,
                            isLoading = false,
                            error = null,
                            searchQuery = searchQuery ?: ""
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun searchProducts(query: String) {
        loadProducts(query.takeIf { it.isNotBlank() })
    }

    // Arama temizleme fonksiyonu
    fun clearSearch() {
        loadProducts(null)
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            try {
                favoriteRepository.isFavorite(product.id).collect { isFavorite ->
                    if (isFavorite) {
                        favoriteRepository.removeFromFavorites(product.id)
                        updateFavoriteState(product.id, false)
                    } else {
                        favoriteRepository.addToFavorites(product)
                        updateFavoriteState(product.id, true)
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Favori işlemi başarısız: ${e.message}")
                }
            }
        }
    }

    private fun updateFavoriteState(productId: String, isFavorite: Boolean) {
        _uiState.update { currentState ->
            val updatedFavorites = currentState.favoriteStates.toMutableMap()
            updatedFavorites[productId] = isFavorite
            currentState.copy(favoriteStates = updatedFavorites)
        }
    }

    fun isFavorite(productId: String) = favoriteRepository.isFavorite(productId)

    private fun loadFavoriteStates() {
        viewModelScope.launch {
            try {
                favoriteRepository.getAllFavorites().collect { favorites ->
                    val favoriteMap = favorites.associate { it.productId to true }
                    _uiState.update { it.copy(favoriteStates = favoriteMap) }
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "Favori durumları yüklenemedi", e)
            }
        }
    }
}

data class HomeUiState(
    val pagingData: PagingData<Product> = PagingData.empty(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val favoriteStates: Map<String, Boolean> = emptyMap(),
    val searchQuery: String = ""
)