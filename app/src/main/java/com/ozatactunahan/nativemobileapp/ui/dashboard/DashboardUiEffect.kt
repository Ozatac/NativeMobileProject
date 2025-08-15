package com.ozatactunahan.nativemobileapp.ui.dashboard

sealed class DashboardUiEffect {
    data class ShowOrderSuccess(val orderNumber: String?) : DashboardUiEffect()
    data class ShowError(val message: String) : DashboardUiEffect()
    data class ShowToast(val message: String) : DashboardUiEffect()
    object NavigateToProfile : DashboardUiEffect()
    object ClearCartSuccess : DashboardUiEffect()
}
