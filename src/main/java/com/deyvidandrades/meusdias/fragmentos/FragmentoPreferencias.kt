package com.deyvidandrades.meusdias.fragmentos

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.assistentes.AssistenteAlarmManager
import com.deyvidandrades.meusdias.assistentes.AssistentePreferencias
import com.deyvidandrades.meusdias.assistentes.AssistentePreferencias.Companion.Chaves
import java.util.Calendar

abstract class FragmentoPreferencias : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val notificacoes: SwitchPreference? = findPreference("notificacoes")
        val notificacaoRecorde: SwitchPreference? = findPreference("notificacao_recorde")
        val notificacaoDiaria: SwitchPreference? = findPreference("notificacao_diaria")
        val preferenciaReset: Preference? = findPreference("reset")

        val debugPrimeiro: EditTextPreference? = findPreference("debug_primeiro")
        val debugRecorde: EditTextPreference? = findPreference("debug_recorde")

        val versao: Preference? = findPreference("versao")

        val seekBarHorario: SeekBarPreference? = findPreference("horario")

        seekBarHorario?.apply {
            value = AssistentePreferencias.carregarPreferencia(context, Chaves.HORARIO)!!.toInt()
        }

        val info = requireContext().packageManager.getPackageInfo(
            requireContext().packageName,
            PackageManager.GET_ACTIVITIES
        )

        versao?.apply {
            summary = "Meus Dias v${info.versionName} (Beta)"
        }

        debugRecorde?.setDefaultValue(
            AssistentePreferencias.carregarPreferencia(
                requireContext(),
                Chaves.RECORDE
            )
        )
        debugPrimeiro?.setDefaultValue(
            AssistentePreferencias.carregarPreferencia(
                requireContext(),
                Chaves.PRIMEIRO
            )
        )

        debugRecorde!!.setOnPreferenceChangeListener { _, newValue ->
            AssistentePreferencias.salvarPreferencia(
                requireContext(),
                Chaves.RECORDE,
                newValue.toString()
            )
            true
        }
        debugPrimeiro!!.setOnPreferenceChangeListener { _, newValue ->
            AssistentePreferencias.salvarPreferencia(
                requireContext(),
                Chaves.PRIMEIRO,
                newValue.toString()
            )
            true
        }

        preferenciaReset!!.setOnPreferenceClickListener {
            AssistentePreferencias.salvarPreferencia(
                requireContext(),
                Chaves.PRIMEIRO,
                Calendar.getInstance().timeInMillis.toString()
            )

            AssistentePreferencias.salvarPreferencia(
                requireContext(),
                Chaves.RECORDE,
                "0"
            )

            true
        }

        notificacoes!!.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == false) {
                notificacaoRecorde!!.isChecked = false
                notificacaoDiaria!!.isChecked = false
            }

            true
        }

        seekBarHorario!!.setOnPreferenceChangeListener { _, _ ->
            AssistenteAlarmManager.cancelarAlarme(requireContext())
            AssistenteAlarmManager.criarAlarme(requireContext())

            true
        }
    }
}
