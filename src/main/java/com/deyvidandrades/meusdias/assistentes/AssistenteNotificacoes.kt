package com.deyvidandrades.meusdias.assistentes

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.activities.MainActivity
import com.deyvidandrades.meusdias.servicos.ActionReceiver
import java.util.Calendar
import java.util.concurrent.TimeUnit

class AssistenteNotificacoes {
    companion object {
        private const val channelId = "meus_dias_1"

        fun criarCanalDeNotificacoes(context: Context) {

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, "Meus dias", importance).apply {
                description = "Progresso do app Meus Dias"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        fun notificacaoDiaria(context: Context) {

            if (AssistentePreferencias.notificaDiaria(context)) {
                val intentSim =
                    Intent(context, ActionReceiver::class.java).putExtra("cumpriu", true)
                val intentNao =
                    Intent(context, ActionReceiver::class.java).putExtra("cumpriu", false)

                val pendingIntentSim = PendingIntent.getBroadcast(
                    context,
                    0,
                    intentSim,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val pendingIntentNao = PendingIntent.getBroadcast(
                    context,
                    1,
                    intentNao,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val sharedPref = context.getSharedPreferences("meus_dias", Context.MODE_PRIVATE)
                val frase = sharedPref.getString("frase", "")
                val primeiro = sharedPref.getString(
                    "primeiro",
                    Calendar.getInstance().timeInMillis.toString()
                )!!.toLong()

                val diferenca = Calendar.getInstance().timeInMillis - primeiro
                val numDias = TimeUnit.MILLISECONDS.toDays(diferenca)

                val builder = Notification.Builder(context, channelId)
                    .setOngoing(true)
                    .setColorized(true)
                    .setAutoCancel(true)
                    .setColor(context.getColor(R.color.accent))
                    .setCategory(Notification.CATEGORY_REMINDER)
                    .setContentTitle("Você cumpriu seu objetivo hoje?")
                    .setContentText("Estou a $numDias dias $frase")
                    .setSmallIcon(R.drawable.round_trending_down_24)
                    .addAction(
                        R.drawable.round_trending_up_24,
                        "Cumpri o objetivo",
                        pendingIntentSim
                    )
                    .addAction(R.drawable.round_trending_down_24, "Deu ruim", pendingIntentNao)

                with(NotificationManagerCompat.from(context)) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    notify(2, builder.build())

                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    val vibrationEffect =
                        VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
                    vibrator.vibrate(vibrationEffect)
                }
            }
        }

        fun notificacaoRecorde(context: Context, texto: String) {

            if (AssistentePreferencias.notificaRecorde(context)) {
                val builder = Notification.Builder(context, channelId)
                    .setAutoCancel(true)
                    .setColorized(true)
                    .setColor(context.getColor(R.color.accent))
                    .setCategory(Notification.CATEGORY_REMINDER)
                    .setContentTitle("Novo recorde!")
                    .setContentText(texto)
                    .setSmallIcon(R.drawable.round_trending_up_24)
                    .setContentIntent(
                        PendingIntent.getActivity(
                            context,
                            0,
                            Intent(context, MainActivity::class.java).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            },
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )

                with(NotificationManagerCompat.from(context)) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    notify(2, builder.build())
                }
            }
        }
    }
}