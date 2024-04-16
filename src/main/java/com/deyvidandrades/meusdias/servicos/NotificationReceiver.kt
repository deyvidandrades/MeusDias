package com.deyvidandrades.meusdias.servicos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.assistentes.NotificacoesUtil
import com.deyvidandrades.meusdias.assistentes.Persistencia

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Persistencia.getInstance(context)

        val objetivoAtual = Persistencia.getObjetivoAtual()
        val numDias = Persistencia.getNumDias()

        NotificacoesUtil.enviarNotificacao(
            context,
            context.getString(R.string.voce_cumpriu_seu_objetivo_hoje),
            context.getString(R.string.estou_a_dias_sem, numDias.toString(), objetivoAtual.titulo),
            NotificacoesUtil.Tipo.DIARIA
        )
    }
}