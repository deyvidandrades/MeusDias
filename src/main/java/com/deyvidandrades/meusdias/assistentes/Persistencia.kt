package com.deyvidandrades.meusdias.assistentes

import android.content.Context
import android.content.SharedPreferences
import com.deyvidandrades.meusdias.objetos.Objetivo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar
import java.util.concurrent.TimeUnit

object Persistencia {

    private var review: Boolean = false
    private var notificacoes: Boolean = true
    private var notificacoesHorario: Int = 8
    private var notificacoesDiarias: Boolean = true
    private var notificacoesRecorde: Boolean = true

    private var preferences: SharedPreferences? = null
    private var arrayObjetivos: ArrayList<Objetivo> = ArrayList()

    init {
        arrayObjetivos.add(Objetivo("sem um objetivo"))
    }

    enum class Paths { NOTIFICACOES, NOTIFICACOES_DIARIAS, NOTIFICACOES_HORARIO, NOTIFICACOES_RECORDE, OBJETIVOS, REVIEW }

    fun getInstance(context: Context) {
        preferences = context.getSharedPreferences("MAIN_DATA", Context.MODE_PRIVATE)
        carregarDados()
    }

    /*FLUXO DADOS*/

    private fun carregarDados() {
        if (preferences != null) {
            review = preferences!!.getBoolean(Paths.REVIEW.name.lowercase(), false)
            notificacoes = preferences!!.getBoolean(Paths.NOTIFICACOES.name.lowercase(), true)
            notificacoesHorario = preferences!!.getInt(Paths.NOTIFICACOES_HORARIO.name.lowercase(), 8)
            notificacoesDiarias = preferences!!.getBoolean(Paths.NOTIFICACOES_DIARIAS.name.lowercase(), true)
            notificacoesRecorde = preferences!!.getBoolean(Paths.NOTIFICACOES_RECORDE.name.lowercase(), true)

            val listaRawObjetivos = preferences!!.getString(Paths.OBJETIVOS.name.lowercase(), "")

            if (listaRawObjetivos != "") {
                val typeTokenObjetivos = object : TypeToken<ArrayList<Objetivo>>() {}.type

                arrayObjetivos.clear()
                arrayObjetivos.addAll(Gson().fromJson(listaRawObjetivos, typeTokenObjetivos))
            } else {
                arrayObjetivos.clear()
                arrayObjetivos.add(Objetivo("sem um objetivo"))
                salvarDados()
            }
        }
    }

    private fun salvarDados() {
        if (preferences != null) {
            with(preferences!!.edit()) {
                putBoolean(Paths.REVIEW.name.lowercase(), review)
                putBoolean(Paths.NOTIFICACOES.name.lowercase(), notificacoes)
                putInt(Paths.NOTIFICACOES_HORARIO.name.lowercase(), notificacoesHorario)
                putBoolean(Paths.NOTIFICACOES_DIARIAS.name.lowercase(), notificacoesDiarias)
                putBoolean(Paths.NOTIFICACOES_RECORDE.name.lowercase(), notificacoesRecorde)

                putString(Paths.OBJETIVOS.name.lowercase(), Gson().toJson(arrayObjetivos))
                commit()
            }

            carregarDados()
        }
    }

    /*FLUXO DEBUG*/

    fun debugSetNumDiasRecorde(num: Int) {
        val objetivoAtual = getObjetivoAtual()

        objetivoAtual.numDiasSeguidos = num
        salvarDados()
    }

    fun debugSetDataInicio(data: Long) {
        val objetivoAtual = getObjetivoAtual()
        val diferenca = Calendar.getInstance().timeInMillis - data

        objetivoAtual.diasCumpridos = TimeUnit.MILLISECONDS.toDays(diferenca).toInt()
        objetivoAtual.dataCriacao = data
        salvarDados()
    }


    /*FLUXO SETTINGS*/

    fun setNotificacoes(value: Boolean) {
        notificacoes = value

        if (!notificacoes) {
            notificacoesRecorde = false
            notificacoesDiarias = false
        }

        salvarDados()
    }

    fun setNotificacoesRecorde(value: Boolean) {
        notificacoesRecorde = value
        salvarDados()
    }

    fun setNotificacoesDiarias(value: Boolean) {
        notificacoesDiarias = value
        salvarDados()
    }

    fun mudarHorarioNotificacoes(novoHorario: Int) {
        notificacoesHorario = novoHorario
        salvarDados()
    }

    fun getHorarioNotificacao() = notificacoesHorario

    fun getReview() = review

    fun setReview() {
        review = true
    }

    /*FLUXO OBJETIVOS*/

    fun getObjetivoAtual() = arrayObjetivos.last()

    fun getObjetivos() = arrayObjetivos

    fun getNumDias(): Int {
        //fixme
        val diferenca = Calendar.getInstance().timeInMillis - getObjetivoAtual().dataCriacao

        return TimeUnit.MILLISECONDS.toDays(diferenca).toInt()
    }

    fun cumprirObjetivo() {
        val objetivoAtual = getObjetivoAtual()
        objetivoAtual.diasCumpridos += 1
        salvarDados()
    }

    fun mudarTitulo(titulo: String) {
        getObjetivoAtual().titulo = titulo
        salvarDados()
    }

    fun checarRecorde(): Boolean {
        val objetivoAtual = getObjetivoAtual()

        if (objetivoAtual.diasCumpridos > objetivoAtual.numDiasSeguidos) {

            objetivoAtual.numDiasSeguidos = objetivoAtual.diasCumpridos
            objetivoAtual.dataRecorde = Calendar.getInstance().timeInMillis
            salvarDados()
            return true
        }

        return false
    }

    fun resetObjetivo() {
        val ultimoObjetivo = getObjetivoAtual()
        arrayObjetivos.add(Objetivo(ultimoObjetivo.titulo))

        salvarDados()
    }

    fun limparDados() {
        arrayObjetivos.clear()
        arrayObjetivos.add(Objetivo("sem um objetivo"))
        salvarDados()
    }
}