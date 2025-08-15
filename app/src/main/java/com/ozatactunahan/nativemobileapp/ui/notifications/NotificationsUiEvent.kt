package com.ozatactunahan.nativemobileapp.ui.notifications

sealed class NotificationsUiEvent {
    object Refresh : NotificationsUiEvent()
    object ClearAll : NotificationsUiEvent()
}
