package com.ozatactunahan.nativemobileapp.ui.dashboard

import com.ozatactunahan.nativemobileapp.data.model.CartItem
import com.ozatactunahan.nativemobileapp.domain.repository.CartRepository
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private lateinit var viewModel: DashboardViewModel
    private lateinit var cartRepository: CartRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        cartRepository = mockk()
        viewModel = DashboardViewModel(cartRepository)
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
        assertTrue(uiState.cartItems.isEmpty())
        assertEquals(0, uiState.cartItemCount)
        assertEquals(0.0, uiState.totalPrice, 0.01)
        assertTrue(uiState.isLoading)
        assertNull(uiState.error)
    }

    @Test
    fun `DashboardUiState should have correct structure`() {
        // Given
        val uiState = DashboardUiState(
            cartItems = listOf(createTestCartItem()),
            cartItemCount = 1,
            totalPrice = 99.99,
            isLoading = false,
            error = null
        )

        // Then
        assertEquals(1, uiState.cartItems.size)
        assertEquals(1, uiState.cartItemCount)
        assertEquals(99.99, uiState.totalPrice, 0.01)
        assertFalse(uiState.isLoading)
        assertNull(uiState.error)
    }

    @Test
    fun `DashboardUiState with custom values should work`() {
        // Given
        val uiState = DashboardUiState(
            cartItems = emptyList(),
            cartItemCount = 0,
            totalPrice = 0.0,
            isLoading = true,
            error = "Test error"
        )

        // Then
        assertTrue(uiState.cartItems.isEmpty())
        assertEquals(0, uiState.cartItemCount)
        assertEquals(0.0, uiState.totalPrice, 0.01)
        assertTrue(uiState.isLoading)
        assertEquals("Test error", uiState.error)
    }

    @Test
    fun `DashboardUiEvent should have correct structure`() {
        // Given
        val updateQuantityEvent = DashboardUiEvent.UpdateQuantity("product1", 2)
        val removeItemEvent = DashboardUiEvent.RemoveItem("product1")
        val clearCartEvent = DashboardUiEvent.ClearCart
        val placeOrderEvent = DashboardUiEvent.PlaceOrder
        val refreshEvent = DashboardUiEvent.Refresh

        // Then
        assertTrue(updateQuantityEvent is DashboardUiEvent.UpdateQuantity)
        assertTrue(removeItemEvent is DashboardUiEvent.RemoveItem)
        assertTrue(clearCartEvent is DashboardUiEvent.ClearCart)
        assertTrue(placeOrderEvent is DashboardUiEvent.PlaceOrder)
        assertTrue(refreshEvent is DashboardUiEvent.Refresh)
    }

    @Test
    fun `DashboardUiEvent UpdateQuantity should have correct values`() {
        // Given
        val event = DashboardUiEvent.UpdateQuantity("product1", 3)

        // Then
        assertEquals("product1", event.productId)
        assertEquals(3, event.newQuantity)
    }

    @Test
    fun `DashboardUiEvent RemoveItem should have correct values`() {
        // Given
        val event = DashboardUiEvent.RemoveItem("product1")

        // Then
        assertEquals("product1", event.productId)
    }

    @Test
    fun `CartItem should have correct structure`() {
        // Given
        val cartItem = createTestCartItem()

        // Then
        assertEquals("1", cartItem.id)
        assertEquals("1", cartItem.productId)
        assertEquals("Test Product", cartItem.productName)
        assertEquals("test.jpg", cartItem.productImage)
        assertEquals("99.99", cartItem.productPrice)
        assertEquals("Test Brand", cartItem.productBrand)
        assertEquals(1, cartItem.quantity)
    }

    @Test
    fun `CartItem with custom values should work`() {
        // Given
        val cartItem = createTestCartItem(
            id = "custom_id",
            productId = "custom_product",
            productName = "Custom Product",
            productPrice = "999.99",
            productBrand = "Custom Brand",
            quantity = 5
        )

        // Then
        assertEquals("custom_id", cartItem.id)
        assertEquals("custom_product", cartItem.productId)
        assertEquals("Custom Product", cartItem.productName)
        assertEquals("999.99", cartItem.productPrice)
        assertEquals("Custom Brand", cartItem.productBrand)
        assertEquals(5, cartItem.quantity)
    }

    private fun createTestCartItem(
        id: String = "1",
        productId: String = "1",
        productName: String = "Test Product",
        productImage: String = "test.jpg",
        productPrice: String = "99.99",
        productBrand: String = "Test Brand",
        quantity: Int = 1
    ) = CartItem(
        id = id,
        productId = productId,
        productName = productName,
        productImage = productImage,
        productPrice = productPrice,
        productBrand = productBrand,
        quantity = quantity
    )
}
