package com.ozatactunahan.nativemobileapp.ui.profile

sealed class ProfileUiEffect {
    data class ShowToast(val message: String) : ProfileUiEffect()
    data class ShowError(val message: String) : ProfileUiEffect()
    data class NavigateToOrderDetail(val order: com.ozatactunahan.nativemobileapp.data.model.Order) : ProfileUiEffect()
    object OrderDeletedSuccess : ProfileUiEffect()
}
