package com.ozatactunahan.nativemobileapp.ui.notifications

sealed class NotificationsUiEvent {
    object Refresh : NotificationsUiEvent()
    object ClearAll : NotificationsUiEvent()
    data class RemoveFromFavorites(val favoriteEntity: com.ozatactunahan.nativemobileapp.data.local.entity.FavoriteEntity) : NotificationsUiEvent()
}
