package com.example.proyecto_ppg

import Personaje
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import kotlin.random.Random

class UpgradeCasinoActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var articulo: Articulo
    private lateinit var tts: TextToSpeech
    private lateinit var barra: UtilBar
    private lateinit var objPersonaje: Personaje
    private lateinit var backButton: Button
    private lateinit var rueda: ImageView
    private lateinit var ruleta: Ruleta
    private lateinit var objeto: ImageView
    private lateinit var rightObjeto: ImageButton
    private lateinit var leftObjeto: ImageButton
    private lateinit var accept: Button
    private lateinit var back: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upgrade_casino)

        initialize()
        sliderObjeto()
        botonesObjeto()

        rueda.setOnClickListener {
            girarRuleta()
        }

        backButton.setOnClickListener {
            val intent = Intent(this, CasinoActivity::class.java)
            intent.putExtra("personaje", objPersonaje)
            startActivity(intent)
        }
    }

    @Suppress("DEPRECATION")
    private fun initialize() {
        back = findViewById(R.id.backUpgrade)
        accept = findViewById(R.id.acceptButton)
        leftObjeto = findViewById(R.id.leftCasino)
        rightObjeto = findViewById(R.id.rightCasino)
        objeto = findViewById(R.id.itemCasinoImage)
        rueda = findViewById(R.id.wheel)
        backButton = findViewById(R.id.backUpgradeButton)
        tts = TextToSpeech(this, this)

        objPersonaje = intent.getParcelableExtra("personaje")!!
        barra = UtilBar(this, objPersonaje)
        barra.setupToolbar(this, R.id.barraOpciones)
        ruleta = Ruleta(this, rueda)
        ruleta.setArticulo(Articulo(Articulo.TipoArticulo.ORO,Articulo.Nombre.MONEDA,0,0,0))
    }

    private fun delayAndSpeak(text: String, tiempo: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            speak(text)
        }, tiempo)
    }

    private fun girarRuleta() {
        val grado = (Random.nextInt(360) + 3600)

        val girar = RotateAnimation(
            0f, grado.toFloat(), rueda.width / 2f, rueda.height / 2f
        )

        girar.duration = 3000
        girar.fillAfter = true

        girar.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                ruleta.opcion(grado)
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        rueda.startAnimation(girar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        barra.onCreateOptionsMenu(menu)
        return true
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val idioma = tts.setLanguage(Locale.US)
            if (idioma == TextToSpeech.LANG_MISSING_DATA || idioma == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS Error", "Lenguaje no admitido")
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    delayAndSpeak(rueda.contentDescription.toString(), 2000)
                    delayAndSpeak(backButton.text.toString(), 4000)
                }, 10000)
            }
        } else {
            Log.e("TTS Error", "Error al cargar")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return barra.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    @Suppress("DEPRECATION")
    private fun speak(ttsText: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    private fun sliderObjeto() {
        val db = DatabaseHelper(this)
        val inventario = objPersonaje.getMochila().getContenido()
        val inventarioHs = objPersonaje.getMochila().getContenido().toHashSet()

        articulo = inventarioHs.elementAt(0)
        objeto.setImageResource(db.getUrl(articulo))
        var pos = 0

        leftObjeto.setOnClickListener {
            if (inventarioHs.indexOf(articulo) > 0) {
                articulo = inventarioHs.elementAt(--pos)
            } else {
                pos = inventarioHs.size - 1
                articulo = inventarioHs.elementAt(pos)
            }
            objeto.setImageResource(db.getUrl(articulo))
        }

        rightObjeto.setOnClickListener {
            if (inventarioHs.indexOf(articulo) < (inventarioHs.size - 1)) {
                articulo = inventarioHs.elementAt(++pos)
            } else {
                pos = 0
                articulo = inventarioHs.elementAt(pos)
            }
            objeto.setImageResource(db.getUrl(articulo))
        }
    }

    private fun botonesObjeto() {
        if (accept.visibility == View.VISIBLE) {
            accept.setOnClickListener {
                if (articulo.getTipoArticulo() == Articulo.TipoArticulo.ORO) {
                    Toast.makeText(this, "No puedes vender ORO", Toast.LENGTH_SHORT).show()
                } else {
                    objeto.visibility = View.GONE
                    accept.visibility = View.GONE
                    rightObjeto.visibility = View.GONE
                    leftObjeto.visibility = View.GONE

                    rueda.visibility = View.VISIBLE
                    back.visibility = View.VISIBLE
                }
            }
        }
        back.setOnClickListener {
            objeto.visibility = View.VISIBLE
            accept.visibility = View.VISIBLE
            rightObjeto.visibility = View.VISIBLE
            leftObjeto.visibility = View.VISIBLE

            rueda.visibility = View.GONE
            back.visibility = View.GONE
        }
    }
}
