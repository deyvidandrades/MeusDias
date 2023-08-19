package com.deyvidandrades.meusdias.activities

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.assistentes.AnimacaoBotao
import com.deyvidandrades.meusdias.assistentes.AssistenteAlarmManager
import com.deyvidandrades.meusdias.assistentes.AssistenteNotificacoes
import com.deyvidandrades.meusdias.assistentes.AssistentePreferencias
import com.deyvidandrades.meusdias.assistentes.AssistentePreferencias.Companion.Chaves
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        sharedPref = this.getSharedPreferences("meus_dias", Context.MODE_PRIVATE)

        val btnSettings: ImageView = findViewById(R.id.btn_settings)
        val btnShare: RelativeLayout = findViewById(R.id.btn_share)

        val tvInfo: TextView = findViewById(R.id.info)
        val etMensagem: EditText = findViewById(R.id.mensagem)

        etMensagem.setText(AssistentePreferencias.carregarPreferencia(this, Chaves.FRASE))
        etMensagem.isFocusable = false

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
                    AssistentePreferencias.salvarPreferencia(
                        this@MainActivity,
                        Chaves.FRASE,
                        etMensagem.toString()
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

            val relativePrint: RelativeLayout = findViewById(R.id.relativePrint)
            val bitmap = saveScreenshot(getBitmapFromView(relativePrint))

            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, Uri.parse(bitmap.toString()))
                type = "image/jpeg"
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }

        AssistenteNotificacoes.criarCanalDeNotificacoes(this)

        verificarRecorde()
        //salvarPreferencia("recorde", "72")
        //salvarPreferencia("primeiro","1687737763000")
        AssistenteAlarmManager.criarAlarme(this)
    }

    private fun saveScreenshot(bitmap: Bitmap): Uri? {
        // Save the screenshot using MediaStore
        val displayName = "screenshot_meus_dias.png"
        val mimeType = "image/png"

        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        val contentResolver = contentResolver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/MeusDias"
            )
        }

        // Save the image
        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        try {
            val outputStream = contentResolver.openOutputStream(imageUri!!)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream?.close()
            return imageUri
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width, view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun verificarRecorde() {
        if (AssistentePreferencias.isRecorde(this)) {
            val tvRecorde: TextView = findViewById(R.id.recorde)
            val tvDias: TextView = findViewById(R.id.dias)
            val linearLayout: LinearLayout = findViewById(R.id.linear_recorde)

            //Carregar preferências
            val frase = AssistentePreferencias.carregarPreferencia(this, Chaves.FRASE)
            val numDias = AssistentePreferencias.carregarDias(this)
            val numRecorde =
                AssistentePreferencias.carregarPreferencia(this, Chaves.RECORDE)

            //Enviar Notificação
            AssistenteNotificacoes.notificacaoRecorde(
                this,
                "Você já está a $numDias dias $frase"
            )

            //Salvar novo recorde
            AssistentePreferencias.salvarPreferencia(
                this,
                Chaves.RECORDE,
                numDias.toString()
            )

            jogarConfetti()

            //Atualizar UI
            if (numDias < 2)
                tvDias.text = String.format(Locale.getDefault(), "%d dia", numDias)
            else
                tvDias.text = String.format(Locale.getDefault(), "%d dias", numDias)

            if (numRecorde!!.toInt() == 0) {
                //linearLayout.visibility = View.GONE
            } else if (numRecorde.toInt() < 2) {
                tvRecorde.text = String.format(Locale.getDefault(), "%d dia.", numRecorde)
                linearLayout.visibility = View.VISIBLE
            } else {
                tvRecorde.text = String.format(Locale.getDefault(), "%d dias.", numRecorde)
                linearLayout.visibility = View.VISIBLE
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