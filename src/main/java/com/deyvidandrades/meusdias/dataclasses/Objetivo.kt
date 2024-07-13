package com.deyvidandrades.meusdias.dataclasses

import java.util.Calendar

data class Objetivo(
    var titulo: String,
    var numDias: Int = 0,
    var numRecorde: Int = 0,
    var data: Long = Calendar.getInstance().timeInMillis
) : Comparable<Objetivo> {
    override fun compareTo(other: Objetivo): Int {
        return data.compareTo(other.data)
    }
}
