package com.deyvidandrades.meusdias.data.helpers

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.deyvidandrades.meusdias.workers.NotificationsWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkManagerHelper {
    const val WORK_ID = "meus_dias_notification_worker"

    fun initWorker(context: Context, hour: Int) {
        Log.d("DWS", "Alarm set: $hour:00")

        val now = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // If the target time has already passed for today, schedule for tomorrow
        if (now.after(targetTime)) {
            targetTime.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = targetTime.timeInMillis - now.timeInMillis

        val dailyWorkRequest = OneTimeWorkRequestBuilder<NotificationsWorker>()
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_ID,
            ExistingWorkPolicy.REPLACE,
            dailyWorkRequest
        )
    }

    fun rescheduleWorker(context: Context, hour: Int) {
        Log.d("DWS", "Alarm reset: $hour:00")

        val now = Calendar.getInstance()
        val nextExecution = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val nextDelay = nextExecution.timeInMillis - now.timeInMillis

        val newRequest = OneTimeWorkRequestBuilder<NotificationsWorker>()
            .setInitialDelay(nextDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_ID,
            ExistingWorkPolicy.REPLACE,
            newRequest
        )
    }

    fun stopWorker(context: Context) {
        Log.d("DWS", "Alarm cancelled.")
        WorkManager.getInstance(context).cancelUniqueWork(WORK_ID)
    }
}