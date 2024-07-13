package com.deyvidandrades.meusdias.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.assistentes.AnimacaoBotao
import com.deyvidandrades.meusdias.assistentes.AssistenteAlarmManager
import com.deyvidandrades.meusdias.assistentes.NotificacoesUtil
import com.deyvidandrades.meusdias.assistentes.Persistencia
import com.deyvidandrades.meusdias.dataclasses.Objetivo
import com.deyvidandrades.meusdias.dialogos.DialogoConfiguracoes
import com.deyvidandrades.meusdias.dialogos.DialogoHistorico
import com.deyvidandrades.meusdias.dialogos.DialogoShare
import com.google.android.play.core.review.ReviewManagerFactory
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var objetivo: Objetivo
    private var editou: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()

        Persistencia.getInstance(this)
        objetivo = Persistencia.getObjetivo()

        AppCompatDelegate.setDefaultNightMode(
            if (Persistencia.getTemaEscuro()) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        val btnOpcoes: Button = findViewById(R.id.btn_opcoes)
        val btnShare: ImageView = findViewById(R.id.btn_share)
        val btnHistorico: Button = findViewById(R.id.btn_historico)

        btnOpcoes.setOnClickListener {
            val customBottomSheet = DialogoConfiguracoes()
            customBottomSheet.show(supportFragmentManager, DialogoConfiguracoes::class.simpleName)
        }

        btnHistorico.setOnClickListener {
            val customBottomSheet = DialogoHistorico()
            customBottomSheet.show(supportFragmentManager, DialogoHistorico::class.simpleName)
        }

        btnShare.setOnClickListener { v ->
            AnimacaoBotao.animar(v)

            val customBottomSheet = DialogoShare()
            customBottomSheet.show(supportFragmentManager, DialogoShare::class.simpleName)
        }
        updateUI()
        configurarPermissoes()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        verificarRecorde()
        objetivo = Persistencia.getObjetivo()

        val tvNumDias: TextView = findViewById(R.id.dias)
        val tvNumRecorde: TextView = findViewById(R.id.recorde)
        val etMensagem: EditText = findViewById(R.id.mensagem)

        etMensagem.setText(objetivo.titulo)

        tvNumDias.text = if (objetivo.numDias < 2)
            getString(R.string.dia, objetivo.numDias.toString())
        else
            getString(R.string.dias, objetivo.numDias.toString())

        tvNumRecorde.text = if (objetivo.numRecorde < 2)
            getString(R.string.dia, objetivo.numRecorde.toString())
        else
            getString(R.string.dias, objetivo.numRecorde.toString())

        configurarTexto(etMensagem)
    }

    private fun configurarPermissoes() {
        val permissionRequestNotificacao =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.POST_NOTIFICATIONS, false) -> {
                        configurarNotificacoes()
                    }
                }
            }

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) -> {
                configurarNotificacoes()
            }

            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionRequestNotificacao.launch(
                        arrayOf(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    )
                }
            }
        }
    }

    private fun configurarNotificacoes() {
        NotificacoesUtil.criarCanalDeNotificacoes(this)
        AssistenteAlarmManager.criarAlarme(this)
    }

    private fun configurarTexto(view: EditText) {
        val tvInfo: TextView = findViewById(R.id.info)

        view.setOnLongClickListener {
            view.setText(view.text.toString().replace(".", ""))
            view.isFocusableInTouchMode = true
            view.findFocus()
            tvInfo.setText(R.string.clique_longo_para_salvar)
            editou = true
            false
        }

        view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (s.toString().contains(".") && editou) {
                    view.isFocusable = false

                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)

                    tvInfo.setText(R.string.clique_longo_para_editar)

                    Persistencia.novoObjetivo(view.text.toString())
                    editou = false
                }

                if (s.toString().contains("\n")) {
                    view.setText(s.toString().replace("\n", ""))
                    view.setSelection(view.text.length)
                }
            }
        })
    }

    private fun verificarRecorde() {
        if (Persistencia.verificarRecorde()) {
            jogarConfetti()

            //Exibir AppReview
            if (!Persistencia.getPlayReview())
                verificarReview()
        }
    }

    private fun verificarReview() {
        val manager = ReviewManagerFactory.create(this)//FakeReviewManager(this)

        val request = manager.requestReviewFlow()

        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result

                val flow = manager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener {
                    Persistencia.setPlayReview()
                }
            }
        }
    }

    private fun jogarConfetti() {
        val confetyView: KonfettiView = findViewById(R.id.konfettiView)
        confetyView.start(
            Party(
                speed = 0f,
                maxSpeed = 30f,
                damping = 0.9f,
                spread = 360,
                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                position = Position.Relative(0.5, 0.3)
            )
        )
    }
}