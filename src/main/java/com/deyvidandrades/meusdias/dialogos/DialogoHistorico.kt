package com.deyvidandrades.meusdias.dialogos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.adaptadores.AdaptadorHistorico
import com.deyvidandrades.meusdias.assistentes.Persistencia
import com.deyvidandrades.meusdias.objetos.Objetivo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DialogoHistorico : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val arrayObjetivos: ArrayList<Objetivo> = ArrayList()
        val dialogoView = inflater.inflate(R.layout.dialogo_historico, container, false)

        val recyclerCards: RecyclerView = dialogoView.findViewById(R.id.recycler_objetivo)

        arrayObjetivos.addAll(Persistencia.getObjetivos())

        recyclerCards.setHasFixedSize(false)
        recyclerCards.adapter = AdaptadorHistorico(requireContext(), arrayObjetivos)
        recyclerCards.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        return dialogoView
    }
}