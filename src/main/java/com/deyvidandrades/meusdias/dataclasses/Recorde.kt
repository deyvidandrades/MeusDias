package com.deyvidandrades.meusdias.dataclasses

import java.util.Calendar

data class Recorde(var titulo: String, var numDias: Int = 0, var data: Long = Calendar.getInstance().timeInMillis) :
    Comparable<Recorde> {
    override fun compareTo(other: Recorde): Int {
        return data.compareTo(other.data)
    }
}
