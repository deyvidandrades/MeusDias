package com.deyvidandrades.meusdias.adaptadores

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.assistentes.AnimacaoBotao
import com.deyvidandrades.meusdias.assistentes.Persistencia
import com.deyvidandrades.meusdias.interfaces.OnItemClickListener

class AdaptadorShare(
    private val context: Context,
    private val arrayList: ArrayList<HashMap<String, String>>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<AdaptadorShare.ViewHolder>() {

    //private var arrayList: ArrayList<HashMap<String, String>> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.item_share, parent, false
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

        val corBG = item["bg"]!!.toColorInt()
        val corText = item["text"]!!.toColorInt()
        val corAccent = item["accent"]!!.toColorInt()

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
        val objetivo = Persistencia.getObjetivo()

        holder.tvFrase.text = objetivo.titulo
        holder.tvNumDias.text = "${objetivo.numDias} ${if (objetivo.numDias > 1) "dias" else "dia"}."
        holder.tvRecorde.text = "${objetivo.numRecorde} ${if (objetivo.numRecorde > 1) "dias" else "dia"}."
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var relativeBackgroundAccent: RelativeLayout = itemView.findViewById(R.id.relative_background_accent)
        var relativeBackground: RelativeLayout = itemView.findViewById(R.id.relative_background)

        var imgSeta: ImageView = itemView.findViewById(R.id.seta)
        var tvEstou: TextView = itemView.findViewById(R.id.tvEstou)
        var tvFrase: TextView = itemView.findViewById(R.id.tvFrase)
        var tvNumDias: TextView = itemView.findViewById(R.id.tvNumDias)
        var tvRecorde: TextView = itemView.findViewById(R.id.tv_recorde)
        var tvFraseRecorde: TextView = itemView.findViewById(R.id.tv_frase_recorde)
        var viewBackgroundMask: View = itemView.findViewById(R.id.view_background_mask)
    }

    //init {
    //this.arrayList = arrayList
    //}
}