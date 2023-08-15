package com.deyvidandrades.meusdias.fragmentos

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.deyvidandrades.meusdias.R
import java.util.Calendar

class FragmentoPreferencias : PreferenceFragmentCompat() {
    private lateinit var sharedPref: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        sharedPref = context?.getSharedPreferences("meus_dias", Context.MODE_PRIVATE)!!

        val notificacoes: SwitchPreference? = findPreference("notificacoes")
        val notificacaoRecorde: SwitchPreference? = findPreference("notificacao_recorde")
        val notificacaoDiaria: SwitchPreference? = findPreference("notificacao_diaria")
        val preferenciaReset: Preference? = findPreference("reset")

        val debugPrimeiro: EditTextPreference? = findPreference("debug_primeiro")
        val debugRecorde: EditTextPreference? = findPreference("debug_recorde")

        debugRecorde?.setDefaultValue(sharedPref.getString("recorde", "0"))
        debugPrimeiro?.setDefaultValue(sharedPref.getString("primeiro", "0"))

        debugRecorde!!.setOnPreferenceChangeListener { _, newValue ->
            salvarPreferencia("recorde", newValue.toString())
            true
        }
        debugPrimeiro!!.setOnPreferenceChangeListener { _, newValue ->
            salvarPreferencia("primeiro", newValue.toString())
            true
        }

        preferenciaReset!!.setOnPreferenceClickListener {
            salvarPreferencia("primeiro", Calendar.getInstance().timeInMillis.toString())
            salvarPreferencia("recorde", "0")

            true
        }

        notificacoes!!.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == false) {
                notificacaoRecorde!!.isChecked = false
                notificacaoDiaria!!.isChecked = false
            }

            true
        }
    }

    private fun salvarPreferencia(key: String, value: String) {
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }
}
