package com.ozatactunahan.nativemobileapp.ui.notifications

sealed class NotificationsUiEffect {
    data class ShowToast(val message: String) : NotificationsUiEffect()
    data class ShowError(val message: String) : NotificationsUiEffect()
    object AllFavoritesCleared : NotificationsUiEffect()
}
