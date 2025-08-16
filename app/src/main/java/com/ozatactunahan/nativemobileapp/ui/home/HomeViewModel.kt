package com.ozatactunahan.nativemobileapp.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.domain.repository.FavoriteRepository
import com.ozatactunahan.nativemobileapp.domain.repository.CartRepository
import com.ozatactunahan.nativemobileapp.data.repository.ProductRepositoryImpl
import com.ozatactunahan.nativemobileapp.domain.usecase.GetProductsUseCase
import androidx.lifecycle.ViewModel
import com.ozatactunahan.nativemobileapp.ui.filter.FilterResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val favoriteRepository: FavoriteRepository,
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<HomeUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private var originalPagingData: PagingData<Product> = PagingData.empty()

    private val _filterState = MutableStateFlow<FilterResult?>(null)
    private val filterState: StateFlow<FilterResult?> = _filterState.asStateFlow()

    init {
        loadProducts()
        loadFavoriteStates()
    }

    fun onUiEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.ProductClick -> {
                viewModelScope.launch {
                    _uiEffect.emit(HomeUiEffect.NavigateToProductDetail(event.product))
                }
            }
            is HomeUiEvent.AddToCartClick -> {
                addToCart(event.product)
            }
            is HomeUiEvent.FavoriteClick -> {
                toggleFavorite(event.product)
            }
            is HomeUiEvent.SearchQuery -> {
                handleSearchQuery(event.query)
            }
            is HomeUiEvent.SearchSubmit -> {
                searchProducts(event.query)
            }
            is HomeUiEvent.FilterButtonClick -> {
                viewModelScope.launch {
                    _uiEffect.emit(HomeUiEffect.NavigateToFilter())
                }
            }
            is HomeUiEvent.FilterButtonLongClick -> {
                clearFilters()
            }
            is HomeUiEvent.ClearSearch -> {
                clearSearch()
            }
            is HomeUiEvent.ClearFilters -> {
                clearFilters()
            }
            is HomeUiEvent.Refresh -> {
                loadProducts()
            }
        }
    }

    fun loadProducts(searchQuery: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getProductsUseCase(searchQuery)
                .cachedIn(viewModelScope)
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "An error occurred"
                        )
                    }
                }
                .collect { pagingData ->
                    originalPagingData = pagingData
                    _uiState.update { currentState ->
                        currentState.copy(
                            pagingData = pagingData,
                            isLoading = false,
                            error = null,
                            searchQuery = searchQuery ?: "",
                            isFiltered = if (searchQuery != null) false else currentState.isFiltered,
                            filteredProducts = if (searchQuery != null) emptyList() else currentState.filteredProducts,
                            refreshTrigger = System.currentTimeMillis()
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

    fun clearSearch() {
        loadProducts(null)
    }

    private fun handleSearchQuery(query: String) {
        when {
            query.isBlank() -> {
                clearSearch()
            }
            query.length >= 3 -> {
                searchProducts(query)
            }
        }
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

    fun applyFilters(filterResult: FilterResult) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val allProducts = productRepository.getProductsSync()

                var filteredProducts = allProducts.filter { product ->
                    val brandMatch = filterResult.selectedBrands.isEmpty() ||
                            filterResult.selectedBrands.contains(product.brand)
                    val modelMatch = filterResult.selectedModels.isEmpty() ||
                            filterResult.selectedModels.contains(product.model)
                    brandMatch && modelMatch
                }
                filteredProducts = when (filterResult.selectedSortOption) {
                    "OLD_TO_NEW" -> filteredProducts.sortedBy { it.createdAt }
                    "NEW_TO_OLD" -> filteredProducts.sortedByDescending { it.createdAt }
                    "PRICE_HIGH_TO_LOW" -> filteredProducts.sortedByDescending { it.price.toDoubleOrNull() ?: 0.0 }
                    "PRICE_LOW_TO_HIGH" -> filteredProducts.sortedBy { it.price.toDoubleOrNull() ?: 0.0 }
                    else -> filteredProducts
                }

                val filteredPagingData = PagingData.from(filteredProducts)

                _filterState.value = filterResult

                _uiState.update { currentState ->
                    val newState = currentState.copy(
                        pagingData = filteredPagingData,
                        filteredProducts = filteredProducts,
                        isFiltered = true,
                        isLoading = false,
                        refreshTrigger = System.currentTimeMillis()
                    )
                    newState
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Filtreleme hatası: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearFilters() {
        viewModelScope.launch {
            _filterState.value = null

            _uiState.update {
                it.copy(
                    pagingData = originalPagingData,
                    filteredProducts = emptyList(),
                    isFiltered = false,
                    refreshTrigger = System.currentTimeMillis()
                )
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            try {
                cartRepository.addToCart(product)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Sepete ekleme hatası: ${e.message}")
                }
            }
        }
    }
}

data class HomeUiState(
    val pagingData: PagingData<Product> = PagingData.empty(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val favoriteStates: Map<String, Boolean> = emptyMap(),
    val searchQuery: String = "",
    val filteredProducts: List<Product> = emptyList(),
    val isFiltered: Boolean = false,
    val refreshTrigger: Long = 0L
)