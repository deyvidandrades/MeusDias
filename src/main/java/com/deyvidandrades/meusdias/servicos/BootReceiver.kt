package com.deyvidandrades.meusdias.servicos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.deyvidandrades.meusdias.assistentes.AssistenteAlarmManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            AssistenteAlarmManager.criarAlarme(context)
        }
    }
}