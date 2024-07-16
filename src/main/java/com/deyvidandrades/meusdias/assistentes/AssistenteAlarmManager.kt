package com.deyvidandrades.meusdias.assistentes

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.deyvidandrades.meusdias.servicos.NotificationReceiver
import java.util.Calendar

object AssistenteAlarmManager {
    private const val ALARM_CODE = 2

    fun criarAlarme(context: Context, novoHorario: Boolean = false) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        Persistencia.getInstance(context)

        val horario = Persistencia.getHorarioNotificacoes()

        val intentVerificacao = PendingIntent.getBroadcast(
            context,
            ALARM_CODE,
            Intent(context, NotificationReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (intentVerificacao != null && novoHorario) {
            alarmManager.cancel(intentVerificacao)
            intentVerificacao.cancel()
            Log.d("DWS.D", "Alarme cancelado")
        }

        if (intentVerificacao == null || novoHorario) {
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                2,
                Intent(context, NotificationReceiver::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )

            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, horario)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )

            Log.d("DWS.D", "Alarme criado: Horario $horario")
        }
    }
}