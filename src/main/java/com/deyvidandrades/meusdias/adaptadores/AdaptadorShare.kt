package com.deyvidandrades.meusdias.adaptadores

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.assistentes.AnimacaoBotao
import com.deyvidandrades.meusdias.assistentes.Persistencia
import com.deyvidandrades.meusdias.interfaces.OnItemClickListener

class AdaptadorShare(
    context: Context,
    arrayList: ArrayList<HashMap<String, String>>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<AdaptadorShare.ViewHolder>() {

    private val context: Context
    private var arrayList: ArrayList<HashMap<String, String>> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.card_share, parent, false
        )
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arrayList[position]

        holder.itemView.setOnClickListener { v ->
            AnimacaoBotao.animar(v)
            listener.onItemClick(v)

            holder.viewBackgroundMask.visibility = View.VISIBLE
        }

        val corBG = Color.parseColor(item["bg"])
        val corText = Color.parseColor(item["text"])
        val corAccent = Color.parseColor(item["accent"])

        holder.relativeBackgroundAccent.backgroundTintList = ColorStateList.valueOf(corAccent)
        holder.viewBackgroundMask.setBackgroundColor(corBG)
        holder.relativeBackground.backgroundTintList = ColorStateList.valueOf(corBG)

        holder.imgSeta.setColorFilter(corAccent)

        holder.tvNumDias.setTextColor(corAccent)
        holder.tvEstou.setTextColor(corText)
        holder.tvFrase.setTextColor(corText)

        holder.tvFraseRecorde.setTextColor(corText)
        holder.tvRecorde.setTextColor(corAccent)

        Persistencia.getInstance(context)
        val objetivoAtual = Persistencia.getObjetivoAtual()

        holder.tvNumDias.text =
            "${objetivoAtual.diasCumpridos} ${if (objetivoAtual.diasCumpridos > 1) "dias" else "dia"}."

        holder.tvFrase.text = objetivoAtual.titulo

        holder.tvRecorde.text =
            "${objetivoAtual.numDiasSeguidos} ${if (objetivoAtual.numDiasSeguidos > 1) "dias" else "dia"}."
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var viewBackgroundMask: View
        var relativeBackgroundAccent: RelativeLayout
        var relativeBackground: RelativeLayout
        var imgSeta: ImageView
        var tvEstou: TextView
        var tvNumDias: TextView
        var tvFrase: TextView
        var tvFraseRecorde: TextView
        var tvRecorde: TextView

        init {
            viewBackgroundMask = itemView.findViewById(R.id.view_background_mask)
            relativeBackgroundAccent = itemView.findViewById(R.id.relative_background_accent)
            relativeBackground = itemView.findViewById(R.id.relative_background)
            imgSeta = itemView.findViewById(R.id.seta)
            tvEstou = itemView.findViewById(R.id.tvEstou)
            tvNumDias = itemView.findViewById(R.id.tvNumDias)
            tvFrase = itemView.findViewById(R.id.tvFrase)
            tvFraseRecorde = itemView.findViewById(R.id.tv_frase_recorde)
            tvRecorde = itemView.findViewById(R.id.tv_recorde)
        }
    }

    init {
        this.context = context
        this.arrayList = arrayList
    }
}