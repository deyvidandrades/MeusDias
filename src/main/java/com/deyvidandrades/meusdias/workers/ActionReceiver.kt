package com.deyvidandrades.meusdias.workers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.data.helpers.NotificationsHelper
import com.deyvidandrades.meusdias.data.model.Goal
import com.deyvidandrades.meusdias.data.repository.NotificationsRepositoryImpl
import com.deyvidandrades.meusdias.data.source.PreferencesDataStore
import com.deyvidandrades.meusdias.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && context != null) {
            val bundle = intent.extras

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(NotificationsHelper.Tipo.DIARIA.ordinal)

            CoroutineScope(Dispatchers.IO).launch {
                val preferencesDataStore = PreferencesDataStore(context.dataStore)
                val notificationsRepository = NotificationsRepositoryImpl(preferencesDataStore)
                val currentGoal = notificationsRepository.getCurrentGoal()

                if (bundle!!.getBoolean(context.getString(R.string.cumpriu), false) && currentGoal != null) {
                    notificationsRepository.goalReached(onNewRecord = { sendNotification(context, currentGoal) })
                } else
                    notificationsRepository.goalFailed()
            }
        }
    }

    private fun sendNotification(context: Context, currentGoal: Goal) {
        NotificationsHelper.enviarNotificacao(
            context,
            context.getString(R.string.voce_tem_um_novo_recorde),
            context.getString(R.string.voc_ja_esta_a_dias, currentGoal.currentRecord + 1, currentGoal.title),
            NotificationsHelper.Tipo.RECORDE,
            false
        )
    }
}