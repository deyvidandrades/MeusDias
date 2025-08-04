package com.deyvidandrades.meusdias.data.repository

import com.deyvidandrades.meusdias.data.model.Goal
import com.deyvidandrades.meusdias.data.source.PreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

interface GoalsRepository {
    fun getGoals(): Flow<List<Goal>>
    suspend fun setGoals(goals: List<Goal>)
}

class GoalsRepositoryImpl(private var preferencesDataStore: PreferencesDataStore) : GoalsRepository {

    override fun getGoals(): Flow<List<Goal>> = preferencesDataStore.loadGoals().map { serializedData ->
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

    override suspend fun setGoals(goals: List<Goal>) {
        preferencesDataStore.saveGoals(Json.encodeToString(goals))
    }
}