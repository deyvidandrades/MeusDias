package com.deyvidandrades.meusdias.assistentes

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.deyvidandrades.meusdias.servicos.NotificationReceiver
import java.util.Calendar

class AssistenteAlarmManager {

    companion object {

        fun criarAlarme(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intentVerificacao = PendingIntent.getBroadcast(
                context,
                2,
                Intent(context, NotificationReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )

            if (intentVerificacao == null) {
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    2,
                    Intent(context, NotificationReceiver::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )

                val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    set(Calendar.HOUR_OF_DAY, 19)
                }

                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis + 1000,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
        }
    }
}