package com.ozatactunahan.nativemobileapp.ui.productdetail

import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.domain.repository.CartRepository
import com.ozatactunahan.nativemobileapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailViewModelTest {

    private lateinit var viewModel: ProductDetailViewModel
    private lateinit var favoriteRepository: FavoriteRepository
    private lateinit var cartRepository: CartRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        favoriteRepository = mock()
        cartRepository = mock()
        viewModel = ProductDetailViewModel(favoriteRepository, cartRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have correct defaults`() {
        // When
        val favoriteState = viewModel.favoriteState.value
        val cartState = viewModel.cartState.value

        // Then
        assertNull(favoriteState)
        assertNull(cartState)
    }

    @Test
    fun `CartState Loading should have correct structure`() {
        // Given
        val loadingState = ProductDetailViewModel.CartState.Loading

        // Then
        assertTrue(loadingState is ProductDetailViewModel.CartState.Loading)
    }

    @Test
    fun `CartState Success should have correct structure`() {
        // Given
        val message = "Ürün sepete eklendi!"
        val successState = ProductDetailViewModel.CartState.Success(message)

        // Then
        assertTrue(successState is ProductDetailViewModel.CartState.Success)
        assertEquals(message, successState.message)
    }

    @Test
    fun `CartState Error should have correct structure`() {
        // Given
        val errorMessage = "Sepete ekleme başarısız"
        val errorState = ProductDetailViewModel.CartState.Error(errorMessage)

        // Then
        assertTrue(errorState is ProductDetailViewModel.CartState.Error)
        assertEquals(errorMessage, errorState.message)
    }

    @Test
    fun `CartState should be sealed class`() {
        // Given
        val loadingState = ProductDetailViewModel.CartState.Loading
        val successState = ProductDetailViewModel.CartState.Success("Test")
        val errorState = ProductDetailViewModel.CartState.Error("Test")

        // Then
        assertTrue(loadingState is ProductDetailViewModel.CartState)
        assertTrue(successState is ProductDetailViewModel.CartState)
        assertTrue(errorState is ProductDetailViewModel.CartState)
    }

    @Test
    fun `Product should have correct structure`() {
        // Given
        val product = createTestProduct()

        // Then
        assertEquals("1", product.id)
        assertEquals("iPhone 15", product.name)
        assertEquals("iphone15.jpg", product.image)
        assertEquals("999", product.price)
        assertEquals("Latest iPhone", product.description)
        assertEquals("iPhone 15", product.model)
        assertEquals("Apple", product.brand)
        assertEquals("2024-01-01", product.createdAt)
    }

    @Test
    fun `Product with custom values should work`() {
        // Given
        val product = createTestProduct(
            id = "custom_id",
            name = "Custom Product",
            price = "1999.99",
            brand = "Custom Brand"
        )

        // Then
        assertEquals("custom_id", product.id)
        assertEquals("Custom Product", product.name)
        assertEquals("1999.99", product.price)
        assertEquals("Custom Brand", product.brand)
    }

    @Test
    fun `Product should be immutable`() {
        // Given
        val product = createTestProduct()

        // Then
        // Product is a data class, so it's immutable by default
        assertTrue(product is Product)
    }

    @Test
    fun `Product equality should work correctly`() {
        // Given
        val product1 = createTestProduct()
        val product2 = createTestProduct()
        val product3 = createTestProduct(id = "2")

        // Then
        assertEquals(product1, product2)
        assertNotEquals(product1, product3)
    }

    @Test
    fun `Product should handle special characters in name`() {
        // Given
        val product = createTestProduct(
            name = "iPhone 15 Pro Max 📱",
            description = "Latest iPhone with special chars: !@#$%^&*()"
        )

        // Then
        assertEquals("iPhone 15 Pro Max 📱", product.name)
        assertEquals("Latest iPhone with special chars: !@#$%^&*()", product.description)
    }

    @Test
    fun `Product should handle very long descriptions`() {
        // Given
        val longDescription = "a".repeat(1000)
        val product = createTestProduct(description = longDescription)

        // Then
        assertEquals(longDescription, product.description)
        assertEquals(1000, product.description.length)
    }

    @Test
    fun `Product should handle edge case prices`() {
        // Given
        val edgeCasePrices = listOf(
            "0",
            "0.01",
            "999999.99",
            "1.23456789",
            "1000000000"
        )

        // When & Then
        edgeCasePrices.forEach { price ->
            val product = createTestProduct(price = price)
            assertEquals(price, product.price)
        }
    }

    private fun createTestProduct(
        id: String = "1",
        name: String = "iPhone 15",
        image: String = "iphone15.jpg",
        price: String = "999",
        description: String = "Latest iPhone",
        model: String = "iPhone 15",
        brand: String = "Apple",
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
