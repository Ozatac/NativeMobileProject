package com.ozatactunahan.nativemobileapp.ui.dashboard

import com.ozatactunahan.nativemobileapp.data.model.CartItem

sealed class DashboardUiEvent {
    data class UpdateQuantity(val productId: String, val newQuantity: Int) : DashboardUiEvent()
    data class RemoveItem(val productId: String) : DashboardUiEvent()
    object ClearCart : DashboardUiEvent()
    object PlaceOrder : DashboardUiEvent()
    object Refresh : DashboardUiEvent()
}
