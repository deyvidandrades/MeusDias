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
import com.deyvidandrades.meusdias.assistentes.Chaves
import java.util.Calendar

class FragmentoPreferencias : PreferenceFragmentCompat() {

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


        val dados = AssistentePreferencias.getPreferencias(requireContext())

        seekBarHorario?.apply {
            value = dados[Chaves.HORARIO.value]!!.toInt()
        }

        val info = requireContext().packageManager.getPackageInfo(
            requireContext().packageName, PackageManager.GET_ACTIVITIES
        )

        versao?.apply {
            summary = "Meus Dias v${info.versionName} (Beta)"
        }

        debugRecorde?.setDefaultValue(
            dados[Chaves.RECORDE.value]!!.toInt()
        )
        debugPrimeiro?.setDefaultValue(
            dados[Chaves.PRIMEIRO.value]!!.toLong()
        )

        debugRecorde!!.setOnPreferenceChangeListener { _, newValue ->
            AssistentePreferencias.setPreferencias(
                requireContext(),
                Chaves.RECORDE,
                newValue.toString()
            )
            true
        }
        debugPrimeiro!!.setOnPreferenceChangeListener { _, newValue ->
            AssistentePreferencias.setPreferencias(
                requireContext(),
                Chaves.PRIMEIRO,
                newValue.toString()
            )
            true
        }

        preferenciaReset!!.setOnPreferenceClickListener {
            AssistentePreferencias.setPreferencias(
                requireContext(),
                Chaves.PRIMEIRO,
                Calendar.getInstance().timeInMillis.toString()
            )

            AssistentePreferencias.setPreferencias(
                requireContext(), Chaves.RECORDE, "0",
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

        seekBarHorario!!.setOnPreferenceChangeListener { _, newValue ->
            AssistenteAlarmManager.cancelarAlarme(requireContext())
            AssistenteAlarmManager.criarAlarme(requireContext())

            AssistentePreferencias.setPreferencias(
                requireContext(), Chaves.HORARIO, newValue.toString()
            )

            true
        }
    }
}
