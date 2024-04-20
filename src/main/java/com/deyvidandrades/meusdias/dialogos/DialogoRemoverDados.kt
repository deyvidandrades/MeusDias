package com.deyvidandrades.meusdias.dialogos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.assistentes.Persistencia
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DialogoRemoverDados : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dialogoView = inflater.inflate(R.layout.dialogo_remover_dados, container, false)

        val btnRemover: Button = dialogoView.findViewById(R.id.btn_deletar)
        val btnCancelar: Button = dialogoView.findViewById(R.id.btn_cancelar)

        btnRemover.setOnClickListener {
            Persistencia.limparDados()
            Toast.makeText(requireContext(), getString(R.string.dados_resetados), Toast.LENGTH_SHORT).show()
            dismiss()
        }

        btnCancelar.setOnClickListener {
            dismiss()
        }

        return dialogoView
    }
}