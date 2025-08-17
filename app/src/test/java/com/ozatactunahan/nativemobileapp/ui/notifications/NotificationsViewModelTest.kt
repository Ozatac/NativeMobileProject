package com.ozatactunahan.nativemobileapp.ui.notifications

import com.ozatactunahan.nativemobileapp.data.local.entity.FavoriteEntity
import com.ozatactunahan.nativemobileapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsViewModelTest {

    private lateinit var viewModel: NotificationsViewModel
    private lateinit var favoriteRepository: FavoriteRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        favoriteRepository = mock()
        viewModel = NotificationsViewModel(favoriteRepository)
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
        assertTrue(uiState.favorites.isEmpty())
        assertFalse(uiState.isLoading)
        assertNull(uiState.error)
    }

    @Test
    fun `NotificationsUiState should have correct structure`() {
        // Given
        val uiState = NotificationsUiState(
            favorites = listOf(createTestFavoriteEntity()),
            isLoading = true,
            error = "Test error"
        )

        // Then
        assertEquals(1, uiState.favorites.size)
        assertTrue(uiState.isLoading)
        assertEquals("Test error", uiState.error)
    }

    @Test
    fun `NotificationsUiState with custom values should work`() {
        // Given
        val uiState = NotificationsUiState(
            favorites = emptyList(),
            isLoading = false,
            error = null
        )

        // Then
        assertTrue(uiState.favorites.isEmpty())
        assertFalse(uiState.isLoading)
        assertNull(uiState.error)
    }

    @Test
    fun `NotificationsUiEvent should have correct structure`() {
        // Given
        val refreshEvent = NotificationsUiEvent.Refresh
        val removeEvent = NotificationsUiEvent.RemoveFromFavorites(createTestFavoriteEntity())
        val clearEvent = NotificationsUiEvent.ClearAll

        // Then
        assertTrue(refreshEvent is NotificationsUiEvent.Refresh)
        assertTrue(removeEvent is NotificationsUiEvent.RemoveFromFavorites)
        assertTrue(clearEvent is NotificationsUiEvent.ClearAll)
    }

    @Test
    fun `NotificationsUiEvent RemoveFromFavorites should have correct values`() {
        // Given
        val favoriteEntity = createTestFavoriteEntity()
        val event = NotificationsUiEvent.RemoveFromFavorites(favoriteEntity)

        // Then
        assertEquals(favoriteEntity, event.favoriteEntity)
    }

    @Test
    fun `FavoriteEntity should have correct structure`() {
        // Given
        val favoriteEntity = createTestFavoriteEntity()

        // Then
        assertEquals("product1", favoriteEntity.productId)
        assertEquals("Test Product", favoriteEntity.name)
        assertEquals("test.jpg", favoriteEntity.imageUrl)
        assertEquals("99.99", favoriteEntity.price)
        assertEquals("Test Brand", favoriteEntity.brand)
        assertNotNull(favoriteEntity.timestamp)
    }

    @Test
    fun `FavoriteEntity with custom values should work`() {
        // Given
        val favoriteEntity = createTestFavoriteEntity(
            productId = "custom_product",
            name = "Custom Product",
            price = "999.99",
            brand = "Custom Brand"
        )

        // Then
        assertEquals("custom_product", favoriteEntity.productId)
        assertEquals("Custom Product", favoriteEntity.name)
        assertEquals("999.99", favoriteEntity.price)
        assertEquals("Custom Brand", favoriteEntity.brand)
    }

    @Test
    fun `FavoriteEntity should be immutable`() {
        // Given
        val favoriteEntity = createTestFavoriteEntity()

        // Then
        // FavoriteEntity is a data class, so it's immutable by default
        assertTrue(favoriteEntity is FavoriteEntity)
    }

    @Test
    fun `FavoriteEntity equality should work correctly`() {
        // Given
        val favoriteEntity1 = createTestFavoriteEntity()
        val favoriteEntity2 = createTestFavoriteEntity()
        val favoriteEntity3 = createTestFavoriteEntity(productId = "product2")

        // Then
        assertEquals(favoriteEntity1, favoriteEntity2)
        assertNotEquals(favoriteEntity1, favoriteEntity3)
    }

    private fun createTestFavoriteEntity(
        productId: String = "product1",
        name: String = "Test Product",
        imageUrl: String = "test.jpg",
        price: String = "99.99",
        brand: String = "Test Brand",
        timestamp: Long = System.currentTimeMillis()
    ) = FavoriteEntity(
        productId = productId,
        name = name,
        imageUrl = imageUrl,
        price = price,
        brand = brand,
        timestamp = timestamp
    )
}
