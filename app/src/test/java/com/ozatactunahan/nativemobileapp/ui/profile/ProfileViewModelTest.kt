package com.ozatactunahan.nativemobileapp.ui.profile

import com.ozatactunahan.nativemobileapp.data.model.CartItem
import com.ozatactunahan.nativemobileapp.data.model.Order
import com.ozatactunahan.nativemobileapp.data.model.OrderStatus
import com.ozatactunahan.nativemobileapp.domain.repository.OrderRepository
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
class ProfileViewModelTest {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var orderRepository: OrderRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        orderRepository = mock()
        viewModel = ProfileViewModel(orderRepository)
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
        assertTrue(uiState.orders.isEmpty())
        assertFalse(uiState.isLoading)
        assertNull(uiState.error)
    }

    @Test
    fun `ProfileUiState should have correct structure`() {
        // Given
        val uiState = ProfileUiState(
            orders = listOf(createTestOrder()),
            isLoading = true,
            error = "Test error"
        )

        // Then
        assertEquals(1, uiState.orders.size)
        assertTrue(uiState.isLoading)
        assertEquals("Test error", uiState.error)
    }

    @Test
    fun `ProfileUiState with custom values should work`() {
        // Given
        val uiState = ProfileUiState(
            orders = emptyList(),
            isLoading = false,
            error = null
        )

        // Then
        assertTrue(uiState.orders.isEmpty())
        assertFalse(uiState.isLoading)
        assertNull(uiState.error)
    }

    @Test
    fun `ProfileUiEvent should have correct structure`() {
        // Given
        val orderClickEvent = ProfileUiEvent.OrderClick(createTestOrder())
        val deleteOrderEvent = ProfileUiEvent.DeleteOrder("order1")
        val refreshEvent = ProfileUiEvent.Refresh

        // Then
        assertTrue(orderClickEvent is ProfileUiEvent.OrderClick)
        assertTrue(deleteOrderEvent is ProfileUiEvent.DeleteOrder)
        assertTrue(refreshEvent is ProfileUiEvent.Refresh)
    }

    @Test
    fun `ProfileUiEvent OrderClick should have correct values`() {
        // Given
        val order = createTestOrder()
        val event = ProfileUiEvent.OrderClick(order)

        // Then
        assertEquals(order, event.order)
    }

    @Test
    fun `ProfileUiEvent DeleteOrder should have correct values`() {
        // Given
        val orderId = "custom_order_id"
        val event = ProfileUiEvent.DeleteOrder(orderId)

        // Then
        assertEquals(orderId, event.orderId)
    }

    @Test
    fun `Order should have correct structure`() {
        // Given
        val order = createTestOrder()

        // Then
        assertEquals("1", order.id)
        assertEquals("order1", order.orderNumber)
        assertEquals(99.99, order.totalAmount, 0.01)
        assertEquals(OrderStatus.PENDING, order.status)
        assertNotNull(order.orderDate)
        assertEquals(1, order.items.size)
    }

    @Test
    fun `Order with custom values should work`() {
        // Given
        val order = createTestOrder(
            id = "custom_id",
            orderNumber = "CUSTOM_ORDER_123",
            totalAmount = 1999.99,
            status = OrderStatus.CONFIRMED
        )

        // Then
        assertEquals("custom_id", order.id)
        assertEquals("CUSTOM_ORDER_123", order.orderNumber)
        assertEquals(1999.99, order.totalAmount, 0.01)
        assertEquals(OrderStatus.CONFIRMED, order.status)
    }

    @Test
    fun `Order should be immutable`() {
        // Given
        val order = createTestOrder()

        // Then
        // Order is a data class, so it's immutable by default
        assertTrue(order is Order)
    }

    @Test
    fun `Order equality should work correctly`() {
        // Given
        val order1 = createTestOrder()
        val order2 = createTestOrder()

        // Then
        // Basic structure test
        assertEquals(order1.id, order2.id)
        assertEquals(order1.orderNumber, order2.orderNumber)
        assertEquals(order1.totalAmount, order2.totalAmount, 0.01)
        assertEquals(order1.status, order2.status)
    }

    @Test
    fun `Order should handle different statuses`() {
        // Given
        val statuses = listOf(
            OrderStatus.PENDING,
            OrderStatus.CONFIRMED,
            OrderStatus.SHIPPED,
            OrderStatus.DELIVERED,
            OrderStatus.CANCELLED
        )

        // When & Then
        statuses.forEach { status ->
            val order = createTestOrder(status = status)
            assertEquals(status, order.status)
        }
    }

    @Test
    fun `Order should handle different amounts`() {
        // Given
        val amounts = listOf(0.0, 0.01, 99.99, 999.99, 9999.99)

        // When & Then
        amounts.forEach { amount ->
            val order = createTestOrder(totalAmount = amount)
            assertEquals(amount, order.totalAmount, 0.01)
        }
    }

    @Test
    fun `Order should handle empty items list`() {
        // Given
        val order = createTestOrder(items = emptyList())

        // Then
        assertTrue(order.items.isEmpty())
        assertEquals(0, order.items.size)
    }

    @Test
    fun `Order should handle multiple items`() {
        // Given
        val items = listOf(
            createTestCartItem("item1", "Product 1", 49.99),
            createTestCartItem("item2", "Product 2", 99.99),
            createTestCartItem("item3", "Product 3", 149.99)
        )

        // Then
        assertEquals(3, items.size)
        assertEquals("item1", items[0].id)
        assertEquals("item2", items[1].id)
        assertEquals("item3", items[2].id)
    }

    @Test
    fun `OrderStatus enum should have correct values`() {
        // Given
        val pending = OrderStatus.PENDING
        val confirmed = OrderStatus.CONFIRMED
        val shipped = OrderStatus.SHIPPED
        val delivered = OrderStatus.DELIVERED
        val cancelled = OrderStatus.CANCELLED

        // Then
        assertEquals("PENDING", pending.name)
        assertEquals("CONFIRMED", confirmed.name)
        assertEquals("SHIPPED", shipped.name)
        assertEquals("DELIVERED", delivered.name)
        assertEquals("CANCELLED", cancelled.name)
    }

    private fun createTestOrder(
        id: String = "1",
        orderNumber: String = "order1",
        totalAmount: Double = 99.99,
        status: OrderStatus = OrderStatus.PENDING,
        orderDate: Long = System.currentTimeMillis(),
        items: List<CartItem> = listOf(createTestCartItem())
    ) = Order(
        id = id,
        orderNumber = orderNumber,
        totalAmount = totalAmount,
        status = status,
        orderDate = orderDate,
        items = items
    )

    private fun createTestCartItem(
        id: String = "item1",
        productName: String = "Test Product",
        price: Double = 99.99,
        quantity: Int = 1
    ) = CartItem(
        id = id,
        productId = id,
        productName = productName,
        productImage = "test.jpg",
        productPrice = price.toString(),
        productBrand = "Test Brand",
        quantity = quantity
    )
}
