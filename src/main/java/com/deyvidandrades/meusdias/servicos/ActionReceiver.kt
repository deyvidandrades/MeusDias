package com.deyvidandrades.meusdias.servicos

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.deyvidandrades.meusdias.assistentes.AssistentePreferencias
import java.util.Calendar

class ActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras

        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(2)

        if (!bundle!!.getBoolean("cumpriu", false)) {
            AssistentePreferencias.salvarPreferencia(
                context,
                AssistentePreferencias.Companion.Chaves.PRIMEIRO,
                Calendar.getInstance().timeInMillis.toString()
            )
        }
    }
}