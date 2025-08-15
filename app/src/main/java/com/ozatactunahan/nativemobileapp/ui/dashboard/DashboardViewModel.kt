package com.ozatactunahan.nativemobileapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozatactunahan.nativemobileapp.data.model.CartItem
import com.ozatactunahan.nativemobileapp.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadCartItems()
        observeCartCount()
        observeTotalPrice()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            cartRepository.getAllCartItems().collect { cartItems ->
                _uiState.update { it.copy(
                    cartItems = cartItems,
                    isLoading = false
                ) }
            }
        }
    }

    private fun observeCartCount() {
        viewModelScope.launch {
            cartRepository.getCartItemCount().collect { count ->
                _uiState.update { it.copy(cartItemCount = count) }
            }
        }
    }

    private fun observeTotalPrice() {
        viewModelScope.launch {
            cartRepository.getTotalAmount().collect { totalPrice ->
                _uiState.update { it.copy(totalPrice = totalPrice ?: 0.0) }
            }
        }
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(productId, newQuantity)
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(productId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }

    fun placeOrder() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val orderNumber = cartRepository.placeOrder()
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = null,
                        orderPlaced = true,
                        orderNumber = orderNumber
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Sipariş verilemedi: ${e.message}"
                    )
                }
            }
        }
    }

    fun resetOrderPlaced() {
        _uiState.update { it.copy(orderPlaced = false, orderNumber = null) }
    }
}

data class DashboardUiState(
    val cartItems: List<CartItem> = emptyList(),
    val cartItemCount: Int = 0,
    val totalPrice: Double = 0.0,
    val isLoading: Boolean = true,
    val error: String? = null,
    val orderPlaced: Boolean = false,
    val orderNumber: String? = null
)