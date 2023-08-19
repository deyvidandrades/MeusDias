package com.deyvidandrades.meusdias.servicos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.deyvidandrades.meusdias.assistentes.AssistenteNotificacoes

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        AssistenteNotificacoes.notificacaoDiaria(context)
    }
}