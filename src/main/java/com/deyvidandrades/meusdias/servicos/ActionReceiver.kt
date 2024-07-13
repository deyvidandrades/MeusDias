package com.deyvidandrades.meusdias.servicos

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.assistentes.NotificacoesUtil
import com.deyvidandrades.meusdias.assistentes.Persistencia

class ActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NotificacoesUtil.Tipo.DIARIA.ordinal)

        Persistencia.getInstance(context)

        if (bundle!!.getBoolean("cumpriu", false))
            Persistencia.cumprirObjetivo()
        else
            Persistencia.falharObjetivo()

        verificarRecorde(context)
    }

    private fun verificarRecorde(context: Context) {
        if (Persistencia.verificarRecorde()) {
            val objetivo = Persistencia.getObjetivo()
            NotificacoesUtil.enviarNotificacao(
                context,
                context.getString(R.string.voce_tem_um_novo_recorde),
                context.getString(R.string.voce_ja_esta_a_dias_sem, objetivo.numRecorde.toString(), objetivo.titulo),
                NotificacoesUtil.Tipo.RECORDE,
                false
            )
        }
    }
}