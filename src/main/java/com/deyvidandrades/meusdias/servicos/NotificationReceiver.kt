package com.deyvidandrades.meusdias.servicos

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.deyvidandrades.meusdias.R
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationReceiver : BroadcastReceiver() {
    private val channelId = "meus_dias_1"

    override fun onReceive(context: Context, intent: Intent) {
        criarNotificacao(context)
    }

    private fun criarNotificacao(context: Context) {
        val intentSim = Intent(context, ActionReceiver::class.java).putExtra("cumpriu", true)
        val intentNao = Intent(context, ActionReceiver::class.java).putExtra("cumpriu", false)

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

        println("DWS.D")

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
            .addAction(R.drawable.round_trending_up_24, "Cumpri o objetivo", pendingIntentSim)
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
            val vibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(vibrationEffect)
        }
    }
}