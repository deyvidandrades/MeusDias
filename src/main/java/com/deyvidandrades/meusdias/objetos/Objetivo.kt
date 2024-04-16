package com.deyvidandrades.meusdias.objetos

import java.util.Calendar

data class Objetivo(
    var titulo: String,
    var dataCriacao: Long = Calendar.getInstance().timeInMillis,
    var dataRecorde: Long = Calendar.getInstance().timeInMillis,
    var numDiasSeguidos: Int = 0,
    var diasCumpridos: Int = 0
)
