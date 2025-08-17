package com.ozatactunahan.nativemobileapp.ui.filter

import com.ozatactunahan.nativemobileapp.data.repository.ProductRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class FilterViewModelTest {

    private lateinit var viewModel: FilterViewModel
    private lateinit var productRepository: ProductRepositoryImpl

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        productRepository = mock()
        viewModel = FilterViewModel(productRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have correct defaults`() {
        // When
        val uiState = viewModel.uiState.value

        // Then
        assertTrue(uiState.sortOptions.isEmpty())
        assertTrue(uiState.brands.isEmpty())
        assertTrue(uiState.models.isEmpty())
        assertTrue(uiState.filteredBrands.isEmpty())
        assertTrue(uiState.filteredModels.isEmpty())
        assertTrue(uiState.allProducts.isEmpty())
        assertTrue(uiState.selectedBrands.isEmpty())
        assertTrue(uiState.selectedModels.isEmpty())
        assertEquals("", uiState.selectedSortOption)
    }

    @Test
    fun `searchBrands with empty query should show all brands`() {
        // Given
        val uiState = viewModel.uiState.value

        // When
        viewModel.searchBrands("")

        // Then
        val newUiState = viewModel.uiState.value
        assertEquals(uiState.brands.size, newUiState.filteredBrands.size)
    }

    @Test
    fun `searchModels with empty query should show all models`() {
        // Given
        val uiState = viewModel.uiState.value

        // When
        viewModel.searchModels("")

        // Then
        val newUiState = viewModel.uiState.value
        assertEquals(uiState.models.size, newUiState.filteredModels.size)
    }

    @Test
    fun `setSelectedSortOption should update sort options correctly`() {
        // Given
        val sortOption = SortOption("test", "Test", false)

        // When
        viewModel.setSelectedSortOption(sortOption)

        // Then
        val uiState = viewModel.uiState.value
        assertEquals(sortOption.id, uiState.selectedSortOption)
    }

    @Test
    fun `toggleBrandSelection should update brand selection correctly`() {
        // Given
        val brand = FilterItem("test", "Test", false)

        // When
        viewModel.toggleBrandSelection(brand, true)

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState.selectedBrands.contains(brand.id))
    }

    @Test
    fun `toggleModelSelection should update model selection correctly`() {
        // Given
        val model = FilterItem("test", "Test", false)

        // When
        viewModel.toggleModelSelection(model, true)

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState.selectedModels.contains(model.id))
    }

    @Test
    fun `toggleBrandSelection should remove brand when unchecked`() {
        // Given
        val brand = FilterItem("test", "Test", false)
        viewModel.toggleBrandSelection(brand, true)

        // When
        viewModel.toggleBrandSelection(brand, false)

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.selectedBrands.contains(brand.id))
    }

    @Test
    fun `toggleModelSelection should remove model when unchecked`() {
        // Given
        val model = FilterItem("test", "Test", false)
        viewModel.toggleModelSelection(model, true)

        // When
        viewModel.toggleModelSelection(model, false)

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.selectedModels.contains(model.id))
    }

    @Test
    fun `applyFilters should return correct FilterResult`() {
        // Given
        val brand = FilterItem("test", "Test", false)
        val model = FilterItem("test", "Test", false)
        val sortOption = SortOption("test", "Test", false)
        
        viewModel.toggleBrandSelection(brand, true)
        viewModel.toggleModelSelection(model, true)
        viewModel.setSelectedSortOption(sortOption)

        // When
        val result = viewModel.applyFilters()

        // Then
        assertEquals(sortOption.id, result.selectedSortOption)
        assertTrue(result.selectedBrands.contains(brand.id))
        assertTrue(result.selectedModels.contains(model.id))
    }

    @Test
    fun `FilterItem should have correct structure`() {
        // Given
        val filterItem = FilterItem("id", "name", true)

        // Then
        assertEquals("id", filterItem.id)
        assertEquals("name", filterItem.name)
        assertTrue(filterItem.isSelected)
    }

    @Test
    fun `SortOption should have correct structure`() {
        // Given
        val sortOption = SortOption("id", "name", true)

        // Then
        assertEquals("id", sortOption.id)
        assertEquals("name", sortOption.name)
        assertTrue(sortOption.isSelected)
    }

    @Test
    fun `FilterResult should have correct structure`() {
        // Given
        val filterResult = FilterResult(
            selectedSortOption = "sort",
            selectedBrands = setOf("brand1", "brand2"),
            selectedModels = setOf("model1")
        )

        // Then
        assertEquals("sort", filterResult.selectedSortOption)
        assertEquals(2, filterResult.selectedBrands.size)
        assertEquals(1, filterResult.selectedModels.size)
        assertTrue(filterResult.selectedBrands.contains("brand1"))
        assertTrue(filterResult.selectedBrands.contains("brand2"))
        assertTrue(filterResult.selectedModels.contains("model1"))
    }
}
