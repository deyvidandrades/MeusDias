package com.deyvidandrades.meusdias.data.repository

import com.deyvidandrades.meusdias.data.model.Goal
import com.deyvidandrades.meusdias.data.model.Settings
import com.deyvidandrades.meusdias.data.source.PreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

interface NotificationsRepository {
    suspend fun getNotificationTime(): Int
    suspend fun getNotificationsEnabled(): Boolean
    suspend fun getCurrentGoal(): Goal?
    suspend fun goalReached(onNewRecord: () -> Unit)
    suspend fun goalFailed()
}

class NotificationsRepositoryImpl(private var preferencesDataStore: PreferencesDataStore) : NotificationsRepository {
    private val defaultSettings = Settings(
        isDarkTheme = true,
        showNotifications = true,
        notificationHour = 8
    )

    private fun getGoals(): Flow<List<Goal>> = preferencesDataStore.loadGoals().map { serializedData ->
        if (serializedData.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                Json.decodeFromString(serializedData)
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    private fun getSettings(): Flow<Settings?> = preferencesDataStore.loadSettings().map { serializedData ->
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

    override suspend fun getNotificationTime(): Int = getSettings().first()?.notificationHour ?: 8

    override suspend fun getNotificationsEnabled(): Boolean = getSettings().first()?.showNotifications ?: true

    override suspend fun getCurrentGoal(): Goal? = getGoals().first().lastOrNull()

    override suspend fun goalReached(onNewRecord: () -> Unit) {
        val goals = getGoals().first()
        val currentGoal = goals.lastOrNull()

        if (currentGoal != null) {
            val numberDays = currentGoal.currentStreak + 1
            val numberStreak = currentGoal.currentRecord + 1

            goals.last().currentStreak = numberDays

            if (numberDays >= numberStreak) {
                goals.last().currentRecord = numberStreak
                onNewRecord.invoke()
            }

            preferencesDataStore.saveGoals(Json.encodeToString(goals))
        }
    }

    override suspend fun goalFailed() {
        val goals = getGoals().first()
        val currentGoal = goals.lastOrNull()

        if (currentGoal != null)
            goals.last().currentStreak = 0

        preferencesDataStore.saveGoals(Json.encodeToString(goals))
    }
}