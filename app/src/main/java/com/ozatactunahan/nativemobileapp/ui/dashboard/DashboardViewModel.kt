package com.ozatactunahan.nativemobileapp.ui.dashboard

import androidx.lifecycle.viewModelScope
import com.ozatactunahan.nativemobileapp.data.model.CartItem
import com.ozatactunahan.nativemobileapp.domain.repository.CartRepository
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<DashboardUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        loadCartItems()
        observeCartCount()
        observeTotalPrice()
    }

    fun onUiEvent(event: DashboardUiEvent) {
        when (event) {
            is DashboardUiEvent.UpdateQuantity -> {
                updateQuantity(event.productId, event.newQuantity)
            }
            is DashboardUiEvent.RemoveItem -> {
                removeFromCart(event.productId)
            }
            is DashboardUiEvent.ClearCart -> {
                clearCart()
            }
            is DashboardUiEvent.PlaceOrder -> {
                placeOrder()
            }
            is DashboardUiEvent.Refresh -> {
                loadCartItems()
            }
        }
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
            try {
                cartRepository.clearCart()
                _uiEffect.emit(DashboardUiEffect.ClearCartSuccess)
            } catch (e: Exception) {
                _uiEffect.emit(DashboardUiEffect.ShowError("Sepet temizlenemedi: ${e.message}"))
            }
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
                        error = null
                    )
                }
                
                _uiEffect.emit(DashboardUiEffect.ShowOrderSuccess(orderNumber))
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Sipariş verilemedi: ${e.message}"
                    )
                }
                
                _uiEffect.emit(DashboardUiEffect.ShowError("Sipariş verilemedi: ${e.message}"))
            }
        }
    }


}

data class DashboardUiState(
    val cartItems: List<CartItem> = emptyList(),
    val cartItemCount: Int = 0,
    val totalPrice: Double = 0.0,
    val isLoading: Boolean = true,
    val error: String? = null
)