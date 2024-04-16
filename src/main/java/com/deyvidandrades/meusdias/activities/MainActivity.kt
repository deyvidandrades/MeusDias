package com.deyvidandrades.meusdias.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.assistentes.AnimacaoBotao
import com.deyvidandrades.meusdias.assistentes.AssistenteAlarmManager
import com.deyvidandrades.meusdias.assistentes.NotificacoesUtil
import com.deyvidandrades.meusdias.assistentes.Persistencia
import com.deyvidandrades.meusdias.dialogos.DialogoHistorico
import com.deyvidandrades.meusdias.dialogos.DialogoShare
import com.google.android.play.core.review.ReviewManagerFactory
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var etMensagem: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        Persistencia.getInstance(this)

        val btnOpcoes: ImageView = findViewById(R.id.btn_opcoes)
        val btnShare: ImageView = findViewById(R.id.btn_share)
        val btnHistorico: Button = findViewById(R.id.btn_historico)
        etMensagem = findViewById(R.id.mensagem)

        btnOpcoes.setOnClickListener { v ->
            AnimacaoBotao.animar(v)

            val intent = Intent(this, ConfiguracoesActivity::class.java)
            startActivity(intent)
        }

        btnShare.setOnClickListener { v ->
            AnimacaoBotao.animar(v)

            val customBottomSheet = DialogoShare()
            customBottomSheet.show(supportFragmentManager, DialogoShare::class.simpleName)
        }

        btnHistorico.setOnClickListener {
            val customBottomSheet = DialogoHistorico()
            customBottomSheet.show(supportFragmentManager, DialogoHistorico::class.simpleName)
        }

        updateUI()
        configurarTexto()
        verificarRecorde()

        NotificacoesUtil.criarCanalDeNotificacoes(this)
        AssistenteAlarmManager.criarAlarme(this)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        val objetivoAtual = Persistencia.getObjetivoAtual()
        val numDias = objetivoAtual.diasCumpridos
        val numDiasRecorde = objetivoAtual.numDiasSeguidos

        val tvNumDias: TextView = findViewById(R.id.dias)
        val tvNumRecorde: TextView = findViewById(R.id.recorde)
        val tvDataRecorde: TextView = findViewById(R.id.infoRecorde)

        etMensagem.setText(objetivoAtual.titulo)

        tvNumDias.text = if (numDias < 2) "$numDias dia" else "$numDias dias"
        tvNumRecorde.text = if (numDiasRecorde < 2) "$numDiasRecorde dia" else "$numDiasRecorde dias"
        tvDataRecorde.text = "Alcançado em ${
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            ).format(Date(objetivoAtual.dataRecorde))
        }"
    }

    private fun configurarTexto() {
        val tvInfo: TextView = findViewById(R.id.info)

        Persistencia.getObjetivoAtual().titulo

        etMensagem.setOnLongClickListener {
            etMensagem.setText(etMensagem.text.toString().replace(".", ""))
            etMensagem.isFocusableInTouchMode = true
            etMensagem.findFocus()
            tvInfo.setText(R.string.clique_longo_para_salvar)
            false
        }

        etMensagem.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (s.toString().contains(".")) {
                    etMensagem.isFocusable = false

                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(etMensagem.windowToken, 0)

                    tvInfo.setText(R.string.clique_longo_para_editar)

                    Persistencia.mudarTitulo(etMensagem.text.toString())
                }

                if (s.toString().contains("\n")) {
                    etMensagem.setText(s.toString().replace("\n", ""))
                    etMensagem.setSelection(etMensagem.text.length)
                }
            }
        })
    }

    private fun verificarRecorde() {
        if (Persistencia.checarRecorde()) {
            jogarConfetti()
            updateUI()

            //Exibir AppReview
            if (!Persistencia.getReview())
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
                    Persistencia.setReview()
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

    @Override
    override fun onResume() {
        super.onResume()
        updateUI()
    }
}