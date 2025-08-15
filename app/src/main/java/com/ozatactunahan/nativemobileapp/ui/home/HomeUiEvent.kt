package com.ozatactunahan.nativemobileapp.ui.home

import com.ozatactunahan.nativemobileapp.data.model.Product

sealed class HomeUiEvent {
    data class ProductClick(val product: Product) : HomeUiEvent()
    data class AddToCartClick(val product: Product) : HomeUiEvent()
    data class FavoriteClick(val product: Product) : HomeUiEvent()
    data class SearchQuery(val query: String) : HomeUiEvent()
    data class SearchSubmit(val query: String) : HomeUiEvent()
    object FilterButtonClick : HomeUiEvent()
    object FilterButtonLongClick : HomeUiEvent()
    object ClearSearch : HomeUiEvent()
    object ClearFilters : HomeUiEvent()
    object Refresh : HomeUiEvent()
}
