package com.deyvidandrades.meusdias.adaptadores

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.objetos.Objetivo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdaptadorHistorico(
    context: Context,
    arrayList: ArrayList<Objetivo>
) :
    RecyclerView.Adapter<AdaptadorHistorico.ViewHolder>() {

    private val context: Context
    private var arrayList: ArrayList<Objetivo> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.item_historico, parent, false
        )
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arrayList[position]

        val dataInicio = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(item.dataCriacao))
        val dataRecorde = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(item.dataRecorde))

        holder.tvFrase.text = item.titulo
        holder.tvNumDias.text = "${item.diasCumpridos} ${if (item.diasCumpridos > 1) "dias" else "dia"}"
        holder.tvNumRecorde.text = "${item.numDiasSeguidos} ${if (item.numDiasSeguidos > 1) "dias" else "dia"}"
        holder.tvDataInicio.text = dataInicio
        holder.tvDataRecorde.text = dataRecorde
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvNumDias: TextView
        var tvNumRecorde: TextView
        var tvFrase: TextView
        var tvDataInicio: TextView
        var tvDataRecorde: TextView

        init {
            tvNumDias = itemView.findViewById(R.id.tvNumDias)
            tvFrase = itemView.findViewById(R.id.tvFrase)
            tvDataInicio = itemView.findViewById(R.id.tv_data_inicio)
            tvDataRecorde = itemView.findViewById(R.id.tv_data_recorde)
            tvNumRecorde = itemView.findViewById(R.id.tvNumRecorde)
        }
    }

    init {
        this.context = context
        this.arrayList = arrayList
    }
}