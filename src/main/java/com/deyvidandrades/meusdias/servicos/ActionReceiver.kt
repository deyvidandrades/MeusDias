package com.deyvidandrades.meusdias.servicos

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.deyvidandrades.meusdias.assistentes.AssistenteNotificacoes
import com.deyvidandrades.meusdias.assistentes.AssistentePreferencias
import com.deyvidandrades.meusdias.assistentes.Chaves
import java.util.Calendar

class ActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras

        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(2)

        if (!bundle!!.getBoolean("cumpriu", false)) {
            AssistentePreferencias.setPreferencias(
                context,
                Chaves.PRIMEIRO,
                Calendar.getInstance().timeInMillis.toString()
            )
            verificarRecorde(context)
        }
    }

    private fun verificarRecorde(context: Context) {
        //Carregar preferências
        val dados = AssistentePreferencias.getPreferencias(context)

        val frase = dados[Chaves.FRASE.value].toString()
        val numDias = dados[Chaves.DIAS.value]!!.toInt()
        val numRecorde = dados[Chaves.RECORDE.value]!!.toInt()

        if (numDias > numRecorde) {

            //Enviar Notificação
            AssistenteNotificacoes.notificacaoRecorde(
                context,
                "Você já está a $numDias dias $frase"
            )

            //Salvar novo recorde
            AssistentePreferencias.setPreferencias(
                context,
                Chaves.RECORDE,
                numDias.toString()
            )

            //Salvar tempo do novo recorde
            AssistentePreferencias.setPreferencias(
                context,
                Chaves.RECORDE_TIME,
                Calendar.getInstance().timeInMillis.toString()
            )
        }
    }
}