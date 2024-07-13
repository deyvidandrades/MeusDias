package com.deyvidandrades.meusdias.dialogos

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.assistentes.Persistencia
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class DialogoDebug : BottomSheetDialogFragment() {
    private var data: Long = 8

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialogo_debug, container, false)

        val btnSalvar: Button = view.findViewById(R.id.btn_salvar)
        val tvData: TextView = view.findViewById(R.id.tv_data_inicio)
        val etDiasRecorde: TextView = view.findViewById(R.id.et_dias_recorde)

        val datePicker =
            MaterialDatePicker.Builder.datePicker().setTitleText(getString(R.string.selecione_a_data)).build()

        etDiasRecorde.text = Persistencia.getObjetivo().numRecorde.toString()
        data = Persistencia.getObjetivo().data

        btnSalvar.setOnClickListener {
            val diferenca = Calendar.getInstance().timeInMillis - data
            val diasRecorde = if (etDiasRecorde.text.toString() != "") etDiasRecorde.text.toString().toInt() else 0

            Persistencia.debugObjetivo(data, TimeUnit.MILLISECONDS.toDays(diferenca).toInt(), diasRecorde)
            dismiss()
        }

        tvData.setOnClickListener {
            datePicker.show((requireContext() as AppCompatActivity).supportFragmentManager, "data")
        }

        datePicker.addOnPositiveButtonClickListener {
            data = it

            val dataFormatada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
            Toast.makeText(requireContext(), "Data alterada para para $dataFormatada", Toast.LENGTH_SHORT).show()
        }
        return view
    }
}