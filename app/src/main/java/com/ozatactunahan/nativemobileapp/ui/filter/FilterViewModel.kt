package com.ozatactunahan.nativemobileapp.ui.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.data.repository.ProductRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val productRepository: ProductRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(FilterUiState())
    val uiState: StateFlow<FilterUiState> = _uiState.asStateFlow()

    init {
        loadFilterData()
    }

    private fun loadFilterData() {
        viewModelScope.launch {
            try {

                val sortOptions = listOf(
                    SortOption(
                        "OLD_TO_NEW",
                        "Eski'den Yeni'ye",
                        false
                    ),
                    SortOption(
                        "NEW_TO_OLD",
                        "Yeni'den Eski'ye",
                        false
                    ),
                    SortOption(
                        "PRICE_HIGH_TO_LOW",
                        "Fiyat (Yüksek-Düşük)",
                        false
                    ),
                    SortOption(
                        "PRICE_LOW_TO_HIGH",
                        "Fiyat (Düşük-Yüksek)",
                        false
                    )
                )

                val allProducts = productRepository.getProductsSync()

                val brandList = allProducts.map { it.brand }.filter { it.isNotBlank() }.distinct().sorted().map {
                    FilterItem(
                        it,
                        it,
                        false
                    )
                }

                val modelList = allProducts.map { it.model }.filter { it.isNotBlank() }.distinct().sorted().map {
                    FilterItem(
                        it,
                        it,
                        false
                    )
                }


                _uiState.update {
                    it.copy(
                        sortOptions = sortOptions,
                        brands = brandList,
                        models = modelList,
                        filteredBrands = brandList,
                        filteredModels = modelList,
                        allProducts = allProducts
                    )
                }

            } catch (_: Exception) {
            }
        }
    }

    fun searchBrands(query: String) {
        _uiState.update { currentState ->
            val filteredBrands = if (query.isBlank()) {
                currentState.brands
            } else {
                currentState.brands.filter {
                    it.name.contains(
                        query,
                        ignoreCase = true
                    )
                }
            }
            currentState.copy(filteredBrands = filteredBrands)
        }
    }

    fun searchModels(query: String) {
        _uiState.update { currentState ->
            val filteredModels = if (query.isBlank()) {
                currentState.models
            } else {
                currentState.models.filter {
                    it.name.contains(
                        query,
                        ignoreCase = true
                    )
                }
            }
            currentState.copy(filteredModels = filteredModels)
        }
    }

    fun setSelectedSortOption(selectedOption: SortOption) {
        _uiState.update { currentState ->
            val updatedSortOptions = currentState.sortOptions.map { option ->
                option.copy(isSelected = option.id == selectedOption.id)
            }
            currentState.copy(
                sortOptions = updatedSortOptions,
                selectedSortOption = selectedOption.id
            )
        }
    }

    fun toggleBrandSelection(
        brand: FilterItem,
        isChecked: Boolean
    ) {
        _uiState.update { currentState ->
            val updatedBrands = currentState.brands.map { item ->
                if (item.id == brand.id) item.copy(isSelected = isChecked) else item
            }
            val selectedBrands = if (isChecked) {
                currentState.selectedBrands + brand.id
            } else {
                currentState.selectedBrands - brand.id
            }
            currentState.copy(
                brands = updatedBrands,
                selectedBrands = selectedBrands
            )
        }
    }

    fun toggleModelSelection(
        model: FilterItem,
        isChecked: Boolean
    ) {
        _uiState.update { currentState ->
            val updatedModels = currentState.models.map { item ->
                if (item.id == model.id) item.copy(isSelected = isChecked) else item
            }
            val selectedModels = if (isChecked) {
                currentState.selectedModels + model.id
            } else {
                currentState.selectedModels - model.id
            }
            currentState.copy(
                models = updatedModels,
                selectedModels = selectedModels
            )
        }
    }

    fun applyFilters(): FilterResult {
        val selectedSortOption = _uiState.value.selectedSortOption
        val selectedBrands = _uiState.value.selectedBrands
        val selectedModels = _uiState.value.selectedModels

        val filterResult = FilterResult(
            selectedSortOption = selectedSortOption,
            selectedBrands = selectedBrands,
            selectedModels = selectedModels
        )

        return filterResult
    }
}

data class FilterUiState(
    val sortOptions: List<SortOption> = emptyList(),
    val brands: List<FilterItem> = emptyList(),
    val models: List<FilterItem> = emptyList(),
    val filteredBrands: List<FilterItem> = emptyList(),
    val filteredModels: List<FilterItem> = emptyList(),
    val allProducts: List<Product> = emptyList(),
    val selectedBrands: Set<String> = emptySet(),
    val selectedModels: Set<String> = emptySet(),
    val selectedSortOption: String = ""
)

data class SortOption(
    val id: String,
    val name: String,
    val isSelected: Boolean
)

data class FilterItem(
    val id: String,
    val name: String,
    val isSelected: Boolean
)

data class FilterResult(
    val selectedSortOption: String,
    val selectedBrands: Set<String>,
    val selectedModels: Set<String>
)
