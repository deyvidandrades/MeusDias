package com.deyvidandrades.meusdias.ui.main

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.data.helpers.BitmapHelper
import com.deyvidandrades.meusdias.data.model.Goal
import com.deyvidandrades.meusdias.data.repository.GoalsRepository
import dev.shreyaspatil.capturable.controller.CaptureController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val goalsRepository: GoalsRepository) : ViewModel() {

    val flowGoals: StateFlow<List<Goal>> = goalsRepository.getGoals().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = listOf(Goal("", 0, 0))
    )

    val flowCurrentGoal: StateFlow<Goal?> = flowGoals.map { goals -> goals.lastOrNull() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = null
    )

    fun createNewGoal(newTitle: String) {
        viewModelScope.launch {
            val goals = flowGoals.value.toMutableList()
            goals.add(Goal(newTitle, 0, 0))

            goalsRepository.setGoals(goals)
        }
    }

    fun updateRecord() {
        viewModelScope.launch {
            val goals = flowGoals.value
            if (goals.isNotEmpty()) {
                val updatedLastGoal = goals.last().copy(currentRecord = goals.last().currentRecord + 1)
                val updated = goals.dropLast(1) + updatedLastGoal

                goalsRepository.setGoals(updated)
            }
        }
    }

    fun shareCurrentGoal(context: Context, captureController: CaptureController, onShared: () -> Unit) {
        viewModelScope.launch {
            val bitmapAsync = captureController.captureAsync()
            try {
                val bitmap = bitmapAsync.await()
                val file = BitmapHelper.saveBitmapToFile(context, bitmap)
                val uri = BitmapHelper.getImageUri(context, file)
                BitmapHelper.shareImage(context, uri)
                onShared.invoke()
            } catch (_: Throwable) {
                Toast.makeText(
                    context,
                    context.getString(R.string.falha_ao_compartilhar_tente_novamente), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun deleteGoal(goalId: Long) {
        viewModelScope.launch {
            val goals = flowGoals.value
            if (goals.isEmpty()) return@launch

            val updatedGoals = goals.filter { item -> item.createdAt == goalId }
            goalsRepository.setGoals(updatedGoals)
        }
    }
}