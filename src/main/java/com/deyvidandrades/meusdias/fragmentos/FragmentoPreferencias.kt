package com.deyvidandrades.meusdias.fragmentos

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.deyvidandrades.meusdias.R

class FragmentoPreferencias : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        //val importarSig: Preference? = findPreference("dados_importar_sig")

        val notificacoes: SwitchPreference? = findPreference("notificacoes")
        val notificacaoRecorde: SwitchPreference? = findPreference("notificacao_recorde")
        val notificacaoDiaria: SwitchPreference? = findPreference("notificacao_diaria")

        notificacoes!!.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == false) {
                notificacaoRecorde!!.isChecked = false
                notificacaoDiaria!!.isChecked = false
            }

            true
        }

        //val seekBarPreference:SeekBarPreference? = findPreference("horario")
        //seekBarPreference?.apply {
        //    showSeekBarValue = true
        //}

    }
}
