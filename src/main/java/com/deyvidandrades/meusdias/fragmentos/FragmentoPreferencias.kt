package com.deyvidandrades.meusdias.fragmentos

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.deyvidandrades.meusdias.R

class FragmentoPreferencias : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        //val importarSig: Preference? = findPreference("dados_importar_sig")

    }
}
