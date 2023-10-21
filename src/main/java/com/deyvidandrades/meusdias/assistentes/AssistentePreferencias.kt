package com.deyvidandrades.meusdias.assistentes

import android.content.Context
import androidx.preference.PreferenceManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

enum class Chaves(val value: String) {
    PRIMEIRO("primeiro"),
    RECORDE("recorde"),
    RECORDE_TIME("recorde_time"),
    FRASE("frase"),
    HORARIO("horario"),
    DIAS("dias")
}

class AssistentePreferencias {
    companion object {

        private fun carregarPreferencia(
            context: Context,
            key: Chaves,
            defaultValue: String
        ): String {
            return context.getSharedPreferences("meus_dias", Context.MODE_PRIVATE)
                .getString(key.value, defaultValue)!!
        }

        private fun salvarPreferencia(
            context: Context,
            key: Chaves,
            value: String,
            defaultValue: String
        ) {
            with(context.getSharedPreferences("meus_dias", Context.MODE_PRIVATE).edit()) {
                if (value != "") putString(key.value, value) else putString(key.value, defaultValue)
                apply()
            }
        }

        fun getPreferencias(context: Context): HashMap<String, String> {
            val map = HashMap<String, String>()

            map[Chaves.PRIMEIRO.value] = carregarPreferencia(
                context,
                Chaves.PRIMEIRO,
                Calendar.getInstance().timeInMillis.toString()
            )

            map[Chaves.RECORDE.value] = carregarPreferencia(
                context,
                Chaves.RECORDE,
                "0"
            )

            map[Chaves.RECORDE_TIME.value] = carregarPreferencia(
                context,
                Chaves.RECORDE_TIME,
                Calendar.getInstance().timeInMillis.toString()
            )

            map[Chaves.FRASE.value] = carregarPreferencia(
                context,
                Chaves.FRASE,
                "sem..."
            )

            map[Chaves.HORARIO.value] = carregarPreferencia(
                context,
                Chaves.HORARIO,
                "19"
            )

            val primeiroDia = map[Chaves.PRIMEIRO.value].toString().toLong()
            val diferenca = Calendar.getInstance().timeInMillis - primeiroDia
            map[Chaves.DIAS.value] = TimeUnit.MILLISECONDS.toDays(diferenca).toString()

            return map
        }

        fun setPreferencias(context: Context, key: Chaves, value: Any) {

            when (key) {
                Chaves.PRIMEIRO -> {
                    salvarPreferencia(
                        context,
                        Chaves.PRIMEIRO,
                        value.toString(),
                        Calendar.getInstance().timeInMillis.toString()
                    )
                }

                Chaves.RECORDE -> {
                    salvarPreferencia(
                        context,
                        Chaves.RECORDE,
                        value.toString(),
                        "0"
                    )
                }

                Chaves.RECORDE_TIME -> {
                    salvarPreferencia(
                        context,
                        Chaves.RECORDE_TIME,
                        value.toString(),
                        Calendar.getInstance().timeInMillis.toString()
                    )
                }

                Chaves.FRASE -> {
                    salvarPreferencia(
                        context,
                        Chaves.FRASE,
                        value.toString(),
                        "sem..."
                    )

                }

                Chaves.HORARIO -> {
                    salvarPreferencia(
                        context,
                        Chaves.HORARIO,
                        value.toString(),
                        "19"
                    )
                }

                else -> {}
            }
        }

        fun setReview(context: Context) {
            with(context.getSharedPreferences("meus_dias", Context.MODE_PRIVATE).edit()) {
                putBoolean("appReview", true)
                apply()
            }
        }

        fun getReview(context: Context): Boolean {
            return context.getSharedPreferences("meus_dias", Context.MODE_PRIVATE)
                .getBoolean("appReview", false)
        }

        fun notificaDiaria(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("notificacao_diaria", true)
        }

        fun notificaRecorde(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("notificacao_recorde", true)
        }

    }
}