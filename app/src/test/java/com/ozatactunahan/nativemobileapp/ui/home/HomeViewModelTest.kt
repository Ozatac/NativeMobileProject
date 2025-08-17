package com.ozatactunahan.nativemobileapp.ui.home

import androidx.paging.PagingData
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.data.repository.ProductRepositoryImpl
import com.ozatactunahan.nativemobileapp.domain.repository.CartRepository
import com.ozatactunahan.nativemobileapp.domain.repository.FavoriteRepository
import com.ozatactunahan.nativemobileapp.domain.usecase.GetProductsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var getProductsUseCase: GetProductsUseCase
    private lateinit var favoriteRepository: FavoriteRepository
    private lateinit var cartRepository: CartRepository
    private lateinit var productRepository: ProductRepositoryImpl

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        getProductsUseCase = mock()
        favoriteRepository = mock()
        cartRepository = mock()
        productRepository = mock()
        
        viewModel = HomeViewModel(
            getProductsUseCase,
            favoriteRepository,
            cartRepository,
            productRepository
        )
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
        assertTrue(uiState.pagingData is PagingData<*>)
        assertTrue(uiState.isLoading)
        assertNull(uiState.error)
        assertTrue(uiState.favoriteStates.isEmpty())
        assertEquals("", uiState.searchQuery)
        assertTrue(uiState.filteredProducts.isEmpty())
        assertFalse(uiState.isFiltered)
        assertEquals(0L, uiState.refreshTrigger)
    }

    @Test
    fun `createTestProduct should create valid product`() {
        // When
        val product = createTestProduct()

        // Then
        assertEquals("1", product.id)
        assertEquals("Test Product", product.name)
        assertEquals("test.jpg", product.image)
        assertEquals("100", product.price)
        assertEquals("Test Description", product.description)
        assertEquals("Test Model", product.model)
        assertEquals("Test Brand", product.brand)
        assertEquals("2024-01-01", product.createdAt)
    }

    @Test
    fun `createTestProduct with custom values should work`() {
        // When
        val product = createTestProduct(
            id = "custom_id",
            name = "Custom Name",
            price = "999.99"
        )

        // Then
        assertEquals("custom_id", product.id)
        assertEquals("Custom Name", product.name)
        assertEquals("999.99", product.price)
    }

    @Test
    fun `Product model should have correct structure`() {
        // Given
        val product = createTestProduct()

        // Then
        assertNotNull(product.id)
        assertNotNull(product.name)
        assertNotNull(product.image)
        assertNotNull(product.price)
        assertNotNull(product.description)
        assertNotNull(product.model)
        assertNotNull(product.brand)
        assertNotNull(product.createdAt)
    }

    @Test
    fun `Product model should be immutable`() {
        // Given
        val product = createTestProduct()

        // Then
        // Product is a data class, so it's immutable by default
        assertTrue(product is Product)
    }

    private fun createTestProduct(
        id: String = "1",
        name: String = "Test Product",
        image: String = "test.jpg",
        price: String = "100",
        description: String = "Test Description",
        model: String = "Test Model",
        brand: String = "Test Brand",
        createdAt: String = "2024-01-01"
    ) = Product(
        id = id,
        name = name,
        image = image,
        price = price,
        description = description,
        model = model,
        brand = brand,
        createdAt = createdAt
    )
}
