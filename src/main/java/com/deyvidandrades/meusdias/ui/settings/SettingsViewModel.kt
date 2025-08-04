package com.deyvidandrades.meusdias.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deyvidandrades.meusdias.data.helpers.WorkManagerHelper
import com.deyvidandrades.meusdias.data.model.Settings
import com.deyvidandrades.meusdias.data.repository.GoalsRepository
import com.deyvidandrades.meusdias.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val goalsRepository: GoalsRepository
) : ViewModel() {
    private val defaultSettings = Settings(
        isDarkTheme = true,
        showNotifications = true,
        notificationHour = 8
    )

    val flowSettings: StateFlow<Settings?> = settingsRepository.getSettings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun updatingDarkTheme(value: Boolean) {
        viewModelScope.launch {
            val settings = flowSettings.value ?: defaultSettings
            val updated = settings.copy(isDarkTheme = value)

            settingsRepository.setSettings(updated)
        }
    }

    fun updatingShowNotifications(context: Context, value: Boolean) {
        viewModelScope.launch {
            val settings = flowSettings.value ?: defaultSettings
            val updated = settings.copy(showNotifications = value)
            settingsRepository.setSettings(updated)

            if (!value)
                WorkManagerHelper.stopWorker(context)
            else
                WorkManagerHelper.initWorker(context, settings.notificationHour)
        }
    }

    fun updatingNotificationTime(context: Context, value: Int) {
        viewModelScope.launch {
            val settings = flowSettings.value ?: defaultSettings
            val updated = settings.copy(notificationHour = value)
            settingsRepository.setSettings(updated)

            WorkManagerHelper.stopWorker(context)
            WorkManagerHelper.initWorker(context, value)
        }
    }

    fun deleteAllGoals() {
        viewModelScope.launch {
            goalsRepository.setGoals(emptyList())
        }
    }
}
