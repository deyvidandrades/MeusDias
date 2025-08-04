package com.deyvidandrades.meusdias.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.data.helpers.NotificationsHelper
import com.deyvidandrades.meusdias.data.helpers.WorkManagerHelper
import com.deyvidandrades.meusdias.data.model.Goal
import com.deyvidandrades.meusdias.data.repository.NotificationsRepositoryImpl
import com.deyvidandrades.meusdias.data.source.PreferencesDataStore
import com.deyvidandrades.meusdias.dataStore

class NotificationsWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        try {
            val preferencesDataStore = PreferencesDataStore(applicationContext.dataStore)
            val notificationsRepository = NotificationsRepositoryImpl(preferencesDataStore)

            val targetHour = notificationsRepository.getNotificationTime()
            val currentGoal: Goal? = notificationsRepository.getCurrentGoal()

            WorkManagerHelper.rescheduleWorker(applicationContext, targetHour)

            if (currentGoal != null) {
                if (notificationsRepository.getNotificationsEnabled())
                    NotificationsHelper.enviarNotificacao(
                        applicationContext,
                        applicationContext.getString(R.string.voce_cumpriu_seu_objetivo_hoje),
                        applicationContext.getString(
                            R.string.estou_a_dias_sem,
                            currentGoal.currentStreak,
                            currentGoal.title
                        ),
                        NotificationsHelper.Tipo.DIARIA
                    )
                return Result.success()
            }

        } catch (_: Exception) {
            return Result.retry()
        }

        return Result.failure()
    }
}