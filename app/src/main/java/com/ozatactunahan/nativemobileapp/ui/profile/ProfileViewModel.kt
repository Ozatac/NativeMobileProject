package com.ozatactunahan.nativemobileapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozatactunahan.nativemobileapp.data.model.Order
import com.ozatactunahan.nativemobileapp.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            orderRepository.getAllOrders().collect { orders ->
                _uiState.update { 
                    it.copy(
                        orders = orders,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            try {
                orderRepository.deleteOrder(orderId)
                // Orders will be updated automatically through Flow
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Sipariş silinemedi: ${e.message}")
                }
            }
        }
    }
}

data class ProfileUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
