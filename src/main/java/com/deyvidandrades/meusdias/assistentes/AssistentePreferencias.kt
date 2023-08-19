package com.deyvidandrades.meusdias.assistentes

import android.content.Context
import androidx.preference.PreferenceManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class AssistentePreferencias {
    companion object {
        enum class Chaves(val value: String) {
            PRIMEIRO("primeiro"),
            RECORDE("recorde"),
            FRASE("frase"),
            HORARIO("horario")
        }

        fun isRecorde(context: Context): Boolean {
            val numRecorde = carregarPreferencia(context, Chaves.RECORDE)

            return carregarDias(context) > numRecorde!!.toInt()
        }

        fun notificaDiaria(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("notificacao_diaria", true)
        }

        fun notificaRecorde(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("notificacao_recorde", true)
        }

        fun carregarDias(context: Context): Int {
            val primeiroDia = carregarPreferencia(context, Chaves.PRIMEIRO)!!.toLong()
            val diferenca = Calendar.getInstance().timeInMillis - primeiroDia
            return TimeUnit.MILLISECONDS.toDays(diferenca).toInt()
        }

        fun carregarPreferencia(context: Context, key: Chaves): String? {
            return context.getSharedPreferences("meus_dias", Context.MODE_PRIVATE)
                .getString(key.value, "0")
        }

        fun salvarPreferencia(context: Context, key: Chaves, value: String) {
            with(context.getSharedPreferences("meus_dias", Context.MODE_PRIVATE).edit()) {
                if (value != "") putString(key.value, value) else putString(key.value, "0")
                apply()
            }
        }
    }
}