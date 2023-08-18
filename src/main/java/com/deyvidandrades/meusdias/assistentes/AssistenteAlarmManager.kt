package com.deyvidandrades.meusdias.assistentes

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
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

                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

                val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    set(Calendar.HOUR_OF_DAY, sharedPreferences.getInt("horario", 19))
                }

                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis + 1000,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )

                println("DWS.D - Alarme criado: Horario ${sharedPreferences.getInt("horario", 19)}")
            }
        }

        fun cancelarAlarme(context: Context) {
            val existingPendingIntent = PendingIntent.getBroadcast(
                context,
                2,
                Intent(context, NotificationReceiver::class.java),
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.cancel(existingPendingIntent)

            println("DWS.D - Alarme cancelado")
        }
    }
}