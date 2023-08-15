package com.deyvidandrades.meusdias.dialogos

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.assistentes.AnimacaoBotao

class DialogoAlertaDados : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.dialogo_alerta_dados, container, false)

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnCriarAnotacao = view.findViewById<TextView>(R.id.btn_ok)

        btnCriarAnotacao.setOnClickListener { v ->
            AnimacaoBotao.animar(v)
            dismiss()
        }

        return view
    }

    companion object {
        fun newInstance(): DialogoAlertaDados {
            return DialogoAlertaDados()
        }
    }
}