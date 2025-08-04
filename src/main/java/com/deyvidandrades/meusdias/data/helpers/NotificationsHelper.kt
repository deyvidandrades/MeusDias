package com.deyvidandrades.meusdias.data.helpers

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.deyvidandrades.meusdias.MainActivity
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.ui.theme.primaryLight
import com.deyvidandrades.meusdias.workers.ActionReceiver

object NotificationsHelper {
    private const val CHANNEL_ID = "meus_dias_1"

    enum class Tipo { DIARIA, RECORDE }

    fun criarCanalDeNotificacoes(context: Context) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, context.getString(R.string.app_name), importance).apply {
            description = context.getString(R.string.canal_para_notificacoes_de_progresso)
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun enviarNotificacao(context: Context, titulo: String, descricao: String, tipo: Tipo, hasActions: Boolean = true) {
        val intentSim = Intent(context, ActionReceiver::class.java)
            .putExtra(context.getString(R.string.cumpriu), true)

        val intentNao = Intent(context, ActionReceiver::class.java)
            .putExtra(context.getString(R.string.cumpriu), false)

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

        val actionSim: NotificationCompat.Action = NotificationCompat.Action.Builder(
            R.drawable.rounded_check_24,
            context.getString(R.string.cumpri_o_objetivo),
            pendingIntentSim
        ).build()

        val actionNao: NotificationCompat.Action = NotificationCompat.Action.Builder(
            R.drawable.rounded_trending_down_24,
            context.getString(R.string.deu_ruim),
            pendingIntentNao
        ).build()

        //CRIANDO A NOTIFICACAO
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setAutoCancel(true)
            .setColorized(true)
            .setShowWhen(true)
            .setColor(primaryLight.toArgb())
            .setCategory(Notification.CATEGORY_REMINDER)
            .setContentTitle(titulo)
            .setContentText(descricao)
            .setSmallIcon(R.drawable.rounded_trending_down_24)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )

        if (hasActions)
            builder.addAction(actionSim).addAction(actionNao)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(tipo.ordinal, builder.build())
        }
    }
}