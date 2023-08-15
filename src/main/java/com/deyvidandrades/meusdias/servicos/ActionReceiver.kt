package com.deyvidandrades.meusdias.servicos

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar

class ActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras

        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(2)

        if (!bundle!!.getBoolean("cumpriu", false)) {
            val sharedPref = context.getSharedPreferences("meus_dias", Context.MODE_PRIVATE)

            with(sharedPref.edit()) {
                putString("primeiro", Calendar.getInstance().timeInMillis.toString())
                apply()
            }
        }
    }
}