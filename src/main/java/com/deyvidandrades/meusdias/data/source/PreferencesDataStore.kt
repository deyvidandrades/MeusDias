package com.deyvidandrades.meusdias.data.source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesDataStore(private val preferencesDataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val KEY_GOALS = stringPreferencesKey("goals")
        val KEY_SETTINGS = stringPreferencesKey("settings")
    }

    fun loadSettings(): Flow<String?> = preferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.KEY_SETTINGS]
    }

    suspend fun saveSettings(serializedData: String) = preferencesDataStore.edit { preferences ->
        preferences[PreferencesKeys.KEY_SETTINGS] = serializedData
    }

    fun loadGoals(): Flow<String?> = preferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.KEY_GOALS]
    }

    suspend fun saveGoals(serializedData: String) = preferencesDataStore.edit { preferences ->
        preferences[PreferencesKeys.KEY_GOALS] = serializedData
    }
}
