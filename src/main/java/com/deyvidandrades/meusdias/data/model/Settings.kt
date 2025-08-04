package com.deyvidandrades.meusdias.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    var isDarkTheme: Boolean,
    var showNotifications: Boolean,
    var notificationHour: Int
)
