package com.deyvidandrades.meusdias.dialogos

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.assistentes.AnimacaoBotao
import com.deyvidandrades.meusdias.assistentes.AssistenteAlarmManager
import com.deyvidandrades.meusdias.assistentes.Persistencia
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.materialswitch.MaterialSwitch

class DialogoConfiguracoes : BottomSheetDialogFragment() {
    private var horario: Int = 8

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialogo_configuracoes, container, false)

        val switchNotificacoes: MaterialSwitch = view.findViewById(R.id.switch_notificacoes)
        val switchTemaEscuro: MaterialSwitch = view.findViewById(R.id.switch_dark_mode)

        val btnSetaEsq: Button = view.findViewById(R.id.btn_seta_esq)
        val btnSetaDir: Button = view.findViewById(R.id.btn_seta_dir)
        val btnSalvar: Button = view.findViewById(R.id.btn_salvar)

        val tvHorarioNotificacao: TextView = view.findViewById(R.id.tv_horario_notificacao)

        val tvRecursosExperimentais: TextView = view.findViewById(R.id.tv_recursos_experimentais)
        val tvLimparDados: TextView = view.findViewById(R.id.tv_limpar_dados)
        val tvTermos: TextView = view.findViewById(R.id.tv_termos)
        val tvVersao: TextView = view.findViewById(R.id.tv_versao)

        val info =
            requireContext().packageManager.getPackageInfo(requireContext().packageName, PackageManager.GET_ACTIVITIES)
        tvVersao.text = "${requireContext().getString(R.string.app_name)} v${info.versionName}"

        switchNotificacoes.isChecked = Persistencia.getNotificacoes()
        switchTemaEscuro.isChecked = Persistencia.getTemaEscuro()
        horario = Persistencia.getHorarioNotificacoes()
        tvHorarioNotificacao.text = if (horario < 10) "0${horario}:00" else "${horario}:00"

        btnSetaEsq.setOnClickListener {
            horario = if (horario - 1 < 0) 23 else horario - 1
            tvHorarioNotificacao.text = if (horario < 10) "0${horario}:00" else "${horario}:00"
        }
        btnSetaDir.setOnClickListener {
            horario = if (horario + 1 > 23) 0 else horario + 1
            tvHorarioNotificacao.text = if (horario < 10) "0${horario}:00" else "${horario}:00"
        }
        btnSalvar.setOnClickListener {
            Persistencia.setTemaEscuro(switchTemaEscuro.isChecked)
            Persistencia.setNotificacoes(switchNotificacoes.isChecked)
            Persistencia.setHorarioNotificacoes(horario)

            AppCompatDelegate.setDefaultNightMode(
                if (Persistencia.getTemaEscuro()) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )

            AssistenteAlarmManager.criarAlarme(requireContext(), true)
            dismiss()
        }

        tvRecursosExperimentais.setOnClickListener {
            val customBottomSheet = DialogoDebug()
            customBottomSheet.show(
                (requireContext() as AppCompatActivity).supportFragmentManager,
                DialogoDebug::class.simpleName
            )
            dismiss()
        }
        tvLimparDados.setOnClickListener {
            val customBottomSheet = DialogoRemoverDados()
            customBottomSheet.show(
                (requireContext() as AppCompatActivity).supportFragmentManager,
                DialogoRemoverDados::class.simpleName
            )
            dismiss()
        }
        tvTermos.setOnClickListener {
            AnimacaoBotao.animar(it)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_termos))))
        }

        return view
    }
}