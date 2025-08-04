package com.deyvidandrades.meusdias.data.helpers

import android.content.Context
import com.deyvidandrades.meusdias.R

object DateHelper {
    fun getFormatedDate(context: Context, createdAt: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - createdAt

        val minutes = diff / (60 * 1000)
        val hours = diff / (60 * 60 * 1000)
        val days = diff / (24 * 60 * 60 * 1000)
        val months = days / 30
        val years = days / 365

        return when {
            minutes < 1 -> context.getString(R.string.agora)
            minutes < 60 -> context.getString(R.string.minutos_atras, minutes, if (minutes == 1L) "" else "s")
            hours < 24 -> context.getString(R.string.horas_atras, hours, if (hours == 1L) "" else "s")
            days < 30 -> context.getString(R.string.dias_atras, days, if (days == 1L) "" else "s")
            days < 365 -> context.getString(R.string.mes_atras, months, if (months == 1L) "" else "s")
            else -> context.getString(R.string.anos_atras, years, if (years == 1L) "" else "s")
        }
    }
}