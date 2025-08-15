package com.ozatactunahan.nativemobileapp.ui.profile

import com.ozatactunahan.nativemobileapp.data.model.Order

sealed class ProfileUiEvent {
    data class OrderClick(val order: Order) : ProfileUiEvent()
    data class DeleteOrder(val orderId: String) : ProfileUiEvent()
    object Refresh : ProfileUiEvent()
}
