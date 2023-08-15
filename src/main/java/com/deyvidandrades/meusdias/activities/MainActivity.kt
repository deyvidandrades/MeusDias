package com.deyvidandrades.meusdias.activities

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.assistentes.AnimacaoBotao
import com.deyvidandrades.meusdias.assistentes.AssistenteAlarmManager
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.io.IOException
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private val channelId = "meus_dias_1"
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

        etMensagem.setText(carregarFrase())
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
                    salvarPreferencia("frase", etMensagem.text.toString())
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

            val intent = Intent("com.instagram.share.ADD_TO_STORY")
            intent.putExtra("source_application", "1410341523172529")
            intent.setDataAndType(Uri.parse(bitmap.toString()), "image/jpg")
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

            startActivityForResult(intent, 0)
        }

        //todo val newFragment = DialogoAlertaDados.newInstance()
        // newFragment.show(supportFragmentManager, "atualizacao")
        //exibirNotificacao()

        createNotificationChannel()
        calcularRecorde()

        //salvarPreferencia("recorde", "72")
        //salvarPreferencia("primeiro","1687737763000")
        AssistenteAlarmManager.criarAlarme(this)
    }

    private fun saveScreenshot(bitmap: Bitmap): Uri? {
        // Save the screenshot using MediaStore
        val displayName = "screenshot_" + System.currentTimeMillis() + ".png"
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

    private fun calcularRecorde() {
        val tvRecorde: TextView = findViewById(R.id.recorde)
        val tvDias: TextView = findViewById(R.id.dias)
        val linearLayout: LinearLayout = findViewById(R.id.linear_recorde)

        val primeiroDia = carregarPrimeiro()
        var numRecorde = carregarRecorde()

        val diferenca = Calendar.getInstance().timeInMillis - primeiroDia
        val numDias = TimeUnit.MILLISECONDS.toDays(diferenca).toInt()

        if (numDias > numRecorde) {
            exibirNotificacao("Você já está a " + numDias + " dias " + carregarFrase())

            salvarPreferencia("recorde", numDias.toString())

            jogarConfetti()

            numRecorde = numDias
        }

        if (numDias < 2)
            tvDias.text = String.format(Locale.getDefault(), "%d dia", numDias)
        else
            tvDias.text = String.format(Locale.getDefault(), "%d dias", numDias)

        if (numRecorde == 0) {
            //linearLayout.visibility = View.GONE
        } else if (numRecorde < 2) {
            tvRecorde.text = String.format(Locale.getDefault(), "%d dia.", numRecorde)
            linearLayout.visibility = View.VISIBLE
        } else {
            tvRecorde.text = String.format(Locale.getDefault(), "%d dias.", numRecorde)
            linearLayout.visibility = View.VISIBLE
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

    private fun exibirNotificacao(texto: String) {

        val builder = Notification.Builder(this, channelId)
            .setColorized(true)
            .setColor(getColor(R.color.accent))
            .setCategory(Notification.CATEGORY_REMINDER)
            .setContentTitle("Novo recorde!")
            .setContentText(texto)
            .setSmallIcon(R.drawable.round_trending_up_24)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(2, builder.build())
        }
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, "Meus dias", importance).apply {
            description = "Progresso do app Meus Dias"
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun salvarPreferencia(key: String, value: String) {
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    private fun carregarRecorde(): Int {
        return sharedPref.getString("recorde", "0")!!.toInt()
    }

    private fun carregarPrimeiro(): Long {
        return sharedPref.getString("primeiro", Calendar.getInstance().timeInMillis.toString())!!
            .toLong()
    }

    private fun carregarFrase(): String {
        return sharedPref.getString("frase", "Sem me dar mal")!!
    }
}