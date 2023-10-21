package com.deyvidandrades.meusdias.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.assistentes.AnimacaoBotao
import com.deyvidandrades.meusdias.assistentes.AssistenteAlarmManager
import com.deyvidandrades.meusdias.assistentes.AssistenteNotificacoes
import com.deyvidandrades.meusdias.assistentes.AssistentePreferencias
import com.deyvidandrades.meusdias.assistentes.Chaves
import com.deyvidandrades.meusdias.dialogos.DialogoShare
import com.google.android.play.core.review.ReviewManagerFactory
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var etMensagem: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        sharedPref = this.getSharedPreferences("meus_dias", Context.MODE_PRIVATE)
        etMensagem = findViewById(R.id.mensagem)

        AssistenteNotificacoes.criarCanalDeNotificacoes(this)

        //FUNCOES DE DEBUG
        //AssistentePreferencias.setPreferencias(this,Chaves.RECORDE, 72)
        //AssistentePreferencias.setPreferencias(this,Chaves.PRIMEIRO, 1687737763000)
        configurarListeners()

        updateUI()

        verificarRecorde()

        AssistenteAlarmManager.criarAlarme(this)
    }

    private fun configurarListeners() {
        val btnSettings: ImageView = findViewById(R.id.btn_settings)
        val btnShare: RelativeLayout = findViewById(R.id.btn_share)
        val tvInfo: TextView = findViewById(R.id.info)

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
                    AssistentePreferencias.setPreferencias(
                        this@MainActivity,
                        Chaves.FRASE,
                        etMensagem.text.toString()
                    )
                }

                if (s.toString().contains("\n")) {
                    etMensagem.setText(s.toString().replace("\n", ""))
                    etMensagem.setSelection(etMensagem.text.length)
                }
            }
        })

        btnSettings.setOnClickListener { v ->
            AnimacaoBotao.animar(v)

            val intent = Intent(this, ConfiguracoesActivity::class.java)
            startActivity(intent)
        }

        btnShare.setOnClickListener { v ->
            AnimacaoBotao.animar(v)

            val customBottomSheet = DialogoShare()
            customBottomSheet.show(supportFragmentManager, "CustomBottomSheet")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        val tvDias: TextView = findViewById(R.id.dias)
        val tvRecorde: TextView = findViewById(R.id.recorde)
        val tvRecordeInfo: TextView = findViewById(R.id.infoRecorde)

        val dados = AssistentePreferencias.getPreferencias(this)

        //Atualizar Frase
        val frase = dados[Chaves.FRASE.value].toString()
        etMensagem.setText(frase)
        etMensagem.isFocusable = false

        //Atualizar Contador
        val dias = dados[Chaves.DIAS.value]!!.toInt()
        tvDias.text = if (dias < 2) "$dias dia" else "$dias dias"

        //Atualizar Recorde
        val recorde = dados[Chaves.RECORDE.value].toString().toInt()
        tvRecorde.text = if (recorde < 2) "$recorde dia." else "$recorde dias."

        //Atualizar data do recorde
        val recordeInfo = dados[Chaves.RECORDE_TIME.value].toString().toLong()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = recordeInfo

        tvRecordeInfo.text = "Alcançado em ${
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            ).format(calendar.time)
        }."
    }

    private fun verificarRecorde() {
        //Carregar preferências
        val dados = AssistentePreferencias.getPreferencias(this)

        val frase = dados[Chaves.FRASE.value].toString()
        val numDias = dados[Chaves.DIAS.value]!!.toInt()
        val numRecorde = dados[Chaves.RECORDE.value]!!.toInt()

        if (numDias > numRecorde) {

            //Enviar Notificação
            AssistenteNotificacoes.notificacaoRecorde(
                this,
                "Você já está a $numDias dias $frase"
            )

            //Salvar novo recorde
            AssistentePreferencias.setPreferencias(
                this,
                Chaves.RECORDE,
                numDias.toString()
            )

            //Salvar tempo do novo recorde
            AssistentePreferencias.setPreferencias(
                this,
                Chaves.RECORDE_TIME,
                Calendar.getInstance().timeInMillis.toString()
            )

            jogarConfetti()
            updateUI()

            //Exibir AppReview
            if (!AssistentePreferencias.getReview(this))
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
                flow.addOnCompleteListener { _ ->
                    println("DWS.D - Review")
                    AssistentePreferencias.setReview(this)
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