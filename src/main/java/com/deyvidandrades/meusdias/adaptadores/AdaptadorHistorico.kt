package com.deyvidandrades.meusdias.adaptadores

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.dataclasses.Recorde
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdaptadorHistorico(private val context: Context, private val arrayList: ArrayList<Recorde> = ArrayList()) :
    RecyclerView.Adapter<AdaptadorHistorico.ViewHolder>() {

    //private var arrayList: ArrayList<Recorde> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.item_historico, parent, false
        )
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arrayList[position]

        holder.tvFrase.text = item.titulo
        holder.tvNumDias.text = "${item.numDias} ${if (item.numDias > 1) "dias" else "dia"}"
        holder.tvData.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(item.data))
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvNumDias: TextView = itemView.findViewById(R.id.tv_num_dias)
        var tvFrase: TextView = itemView.findViewById(R.id.tv_frase)
        var tvData: TextView = itemView.findViewById(R.id.tv_data)
    }

    //init {
    //    this.arrayList = arrayList
    //}
}