package com.ozatactunahan.nativemobileapp.ui.home

import com.ozatactunahan.nativemobileapp.data.model.Product

sealed class HomeUiEffect {
    data class NavigateToProductDetail(val product: Product) : HomeUiEffect()
    data class NavigateToFilter(val filterResult: Any? = null) : HomeUiEffect()
    data class ShowToast(val message: String) : HomeUiEffect()
    data class ShowSnackbar(val message: String) : HomeUiEffect()
    data class ShowError(val message: String) : HomeUiEffect()
    object ClearSearchView : HomeUiEffect()
}
