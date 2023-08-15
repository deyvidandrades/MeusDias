package com.deyvidandrades.meusdias.activities

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.assistentes.AnimacaoBotao
import com.deyvidandrades.meusdias.fragmentos.FragmentoPreferencias

class ConfiguracoesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, FragmentoPreferencias())
            .commit()

        val ivVoltar: ImageView = findViewById(R.id.iv_voltar)

        ivVoltar.setOnClickListener { v ->
            AnimacaoBotao.animar(v)
            finish()
        }
    }
}