package com.deyvidandrades.meusdias.assistentes

import android.content.Context
import android.content.SharedPreferences
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.dataclasses.Objetivo
import com.deyvidandrades.meusdias.dataclasses.Recorde
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Persistencia {

    private var isPlayReview: Boolean = false
    private var isTemaEscuro: Boolean = false
    private var isNotificacoes: Boolean = false
    private var horarioNotificacoes: Int = 8

    private var preferences: SharedPreferences? = null

    private var arrayRecordes: ArrayList<Recorde> = ArrayList()
    private lateinit var objetivo: Objetivo

    enum class Paths { OBJETIVO, RECORDES, NOTIFICACOES, HORARIO_NOTIFICACOES, TEMA_ESCURO, PLAY_REVIEW }

    fun getInstance(context: Context) {
        objetivo = Objetivo(context.getString(R.string.sem_um_objetivo))
        preferences = context.getSharedPreferences("MAIN_DATA", Context.MODE_PRIVATE)

        carregarDados()
    }

    /*FLUXO DADOS*/

    private fun carregarDados() {
        if (preferences != null) {
            isPlayReview = preferences!!.getBoolean(Paths.PLAY_REVIEW.name.lowercase(), false)
            isTemaEscuro = preferences!!.getBoolean(Paths.TEMA_ESCURO.name.lowercase(), false)
            isNotificacoes = preferences!!.getBoolean(Paths.NOTIFICACOES.name.lowercase(), true)
            horarioNotificacoes = preferences!!.getInt(Paths.HORARIO_NOTIFICACOES.name.lowercase(), 8)

            val listaRawRecordes = preferences!!.getString(Paths.RECORDES.name.lowercase(), "")
            val objetivoRaw = preferences!!.getString(Paths.OBJETIVO.name.lowercase(), "")

            if (listaRawRecordes != "") {
                val typeTokenObjetivos = object : TypeToken<ArrayList<Recorde>>() {}.type
                arrayRecordes.clear()
                arrayRecordes.addAll(Gson().fromJson(listaRawRecordes, typeTokenObjetivos))
            }

            if (objetivoRaw != "")
                objetivo = Gson().fromJson(objetivoRaw, object : TypeToken<Objetivo>() {}.type)
        }
    }

    private fun salvarDados() {
        if (preferences != null) {
            with(preferences!!.edit()) {
                putInt(Paths.HORARIO_NOTIFICACOES.name.lowercase(), horarioNotificacoes)
                putBoolean(Paths.NOTIFICACOES.name.lowercase(), isNotificacoes)
                putBoolean(Paths.PLAY_REVIEW.name.lowercase(), isPlayReview)
                putBoolean(Paths.TEMA_ESCURO.name.lowercase(), isTemaEscuro)

                putString(Paths.RECORDES.name.lowercase(), Gson().toJson(arrayRecordes))
                putString(Paths.OBJETIVO.name.lowercase(), Gson().toJson(objetivo))
                commit()
            }
            carregarDados()
        }
    }

    fun debugObjetivo(data: Long, numDias: Int, numRecorde: Int) {
        objetivo.data = data
        objetivo.numDias = numDias
        objetivo.numRecorde = numRecorde
        salvarDados()
    }

    /*FLUXO SETTINGS*/

    fun getTemaEscuro() = isTemaEscuro

    fun getNotificacoes() = isNotificacoes

    fun getHorarioNotificacoes() = horarioNotificacoes

    fun setNotificacoes(value: Boolean) {
        isNotificacoes = value
        salvarDados()
    }

    fun setTemaEscuro(value: Boolean) {
        isTemaEscuro = value
        salvarDados()
    }

    fun setHorarioNotificacoes(novoHorario: Int) {
        horarioNotificacoes = novoHorario
        salvarDados()
    }

    fun getHorarioNotificacao() = horarioNotificacoes

    fun getPlayReview() = isPlayReview

    fun setPlayReview() {
        isPlayReview = true
    }

    /*FLUXO OBJETIVO*/

    fun getObjetivo() = objetivo

    fun novoObjetivo(novoTitulo: String) {
        objetivo = Objetivo(novoTitulo)
        salvarDados()
    }

    fun cumprirObjetivo() {
        objetivo.numDias += 1
        salvarDados()
    }

    fun falharObjetivo() {
        arrayRecordes.add(Recorde(objetivo.titulo, objetivo.numRecorde))
        objetivo.numDias = 0
        salvarDados()
    }

    /*FLUXO RECORDE*/

    fun getRecordes() = arrayRecordes

    fun limparRecordes() {
        arrayRecordes.clear()
        objetivo.numDias = 0
        objetivo.numRecorde = 0
        salvarDados()
    }

    fun verificarRecorde(): Boolean {
        if (objetivo.numDias > objetivo.numRecorde) {
            objetivo.numRecorde = objetivo.numDias
            salvarDados()
            return true
        }
        return false
    }
}