package com.deyvidandrades.meusdias.data.repository

import com.deyvidandrades.meusdias.data.model.Settings
import com.deyvidandrades.meusdias.data.source.PreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

interface SettingsRepository {
    fun getSettings(): Flow<Settings?>
    suspend fun setSettings(settings: Settings)
}

class SettingsRepositoryImpl(private var preferencesDataStore: PreferencesDataStore) : SettingsRepository {
    private val defaultSettings = Settings(
        isDarkTheme = false,
        showNotifications = true,
        notificationHour = 8
    )

    override fun getSettings(): Flow<Settings?> = preferencesDataStore.loadSettings().map { serializedData ->
        if (serializedData.isNullOrEmpty()) {
            defaultSettings
        } else {
            try {
                Json.decodeFromString(serializedData)
            } catch (_: Exception) {
                defaultSettings
            }
        }
    }

    override suspend fun setSettings(settings: Settings) {
        preferencesDataStore.saveSettings(Json.encodeToString(settings))
    }
}