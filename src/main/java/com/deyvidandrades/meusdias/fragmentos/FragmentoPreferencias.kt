package com.deyvidandrades.meusdias.fragmentos

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.assistentes.AssistenteAlarmManager
import com.deyvidandrades.meusdias.assistentes.Persistencia
import com.deyvidandrades.meusdias.dialogos.DialogoRemoverDados
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FragmentoPreferencias : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val notificacaoRecorde: SwitchPreference? = findPreference("notificacao_recorde")
        val notificacaoDiaria: SwitchPreference? = findPreference("notificacao_diaria")
        val notificacoes: SwitchPreference? = findPreference("notificacoes")
        val preferenciaReset: Preference? = findPreference("reset")

        val debugRecorde: EditTextPreference? = findPreference("debug_recorde")
        val debugNumDias: EditTextPreference? = findPreference("debug_num_dias")
        val debugPrimeiro: Preference? = findPreference("debug_primeiro")

        val versao: Preference? = findPreference("versao")
        val preferenciaPrivacidade: Preference? = findPreference("privacidade")

        val seekBarHorario: SeekBarPreference? = findPreference("horario")

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione uma data")
            .build()

        seekBarHorario?.apply {
            value = Persistencia.getHorarioNotificacao()
        }

        val info = requireContext().packageManager.getPackageInfo(
            requireContext().packageName, PackageManager.GET_ACTIVITIES
        )

        versao?.apply {
            summary = "Meus Dias v${info.versionName}"
        }
        debugNumDias?.apply {
            text = Persistencia.getObjetivoAtual().diasCumpridos.toString()
        }
        debugRecorde?.apply {
            text = Persistencia.getObjetivoAtual().numDiasSeguidos.toString()
        }

        notificacoes!!.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == false) {
                notificacaoRecorde!!.isChecked = false
                notificacaoDiaria!!.isChecked = false

            }

            Persistencia.setNotificacoes(newValue as Boolean)

            true
        }

        notificacaoDiaria!!.setOnPreferenceChangeListener { _, newValue ->
            Persistencia.setNotificacoesDiarias(newValue as Boolean)

            true
        }

        notificacaoRecorde!!.setOnPreferenceChangeListener { _, newValue ->
            Persistencia.setNotificacoesRecorde(newValue as Boolean)

            true
        }

        preferenciaReset!!.setOnPreferenceClickListener {
            val customBottomSheet = DialogoRemoverDados()
            customBottomSheet.show(parentFragmentManager, DialogoRemoverDados::class.simpleName)
            true
        }

        seekBarHorario!!.setOnPreferenceChangeListener { _, newValue ->
            AssistenteAlarmManager.cancelarAlarme(requireContext())
            AssistenteAlarmManager.criarAlarme(requireContext())

            Persistencia.mudarHorarioNotificacoes(newValue as Int)
            true
        }

        preferenciaPrivacidade!!.setOnPreferenceClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.uld_politica)))
            startActivity(browserIntent)
            true
        }

        debugPrimeiro!!.setOnPreferenceClickListener {
            datePicker.show(parentFragmentManager, "data")
            true
        }

        debugRecorde!!.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER
        }
        debugRecorde.setOnPreferenceChangeListener { _, newValue ->
            Persistencia.debugSetNumDiasRecorde(newValue.toString().toInt())
            Toast.makeText(requireContext(), "Recorde alterado para ${newValue.toString().toInt()}", Toast.LENGTH_SHORT)
                .show()
            true
        }

        debugNumDias!!.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER
        }
        debugNumDias.setOnPreferenceChangeListener { _, newValue ->
            Persistencia.debugSetNumDiasCumpridos(newValue.toString().toInt())
            Toast.makeText(requireContext(), "NumDias alterado para ${newValue.toString().toInt()}", Toast.LENGTH_SHORT)
                .show()

            true
        }

        datePicker.addOnPositiveButtonClickListener {
            Persistencia.debugSetDataInicio(it)
            val data = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))

            Toast.makeText(requireContext(), "Data alterada para para $data", Toast.LENGTH_SHORT)
                .show()
        }
    }
}
