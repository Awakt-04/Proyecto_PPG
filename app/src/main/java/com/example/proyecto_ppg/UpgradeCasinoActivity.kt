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
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import kotlin.random.Random

class UpgradeCasinoActivity :AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var barra: UtilBar
    private lateinit var objPersonaje: Personaje
    private lateinit var backButton: Button
    private lateinit var rueda: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upgrade_casino)

        initialize()

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
        rueda = findViewById(R.id.wheel)
        backButton = findViewById(R.id.backUpgradeButton)
        tts = TextToSpeech(this, this)

        objPersonaje = intent.getParcelableExtra("personaje")!!
        barra = UtilBar(this, objPersonaje)
        barra.setupToolbar(this, R.id.barraOpciones)
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
//                val selectedOption = opcion(grado)
//                Toast.makeText(
//                    this@UpgradeCasinoActivity,
//                    "Selected Option: $selectedOption",
//                    Toast.LENGTH_SHORT
//                ).show()
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
            if (idioma == TextToSpeech.LANG_MISSING_DATA
                || idioma == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
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

//    private fun opcion(grados: Int): String {
//        val opcion1: Pair<Int, Int>
//        val opcion2: Pair<Int, Int>
//
////        if (Articulo.getNivel() == 1) {
////            opcion1 = Pair(0, (360 * 0.4).toInt())
////            opcion2 = Pair(opcion1.second, 360)
////            rueda.setImageResource(R.drawable.wheel_forty)
////        } else {
////
////            opcion1 = Pair(0, (360 * 0.2).toInt())
////            opcion2 = Pair(opcion1.second, 360)
////            rueda.setImageResource(R.drawable.wheel_twenty)
////        }
//
//            // Verificar en qué rango de grados cayó la rueda
////            return when (grados) {
////                in opcion1.first until opcion1.second -> "Se mejora el artículo"
////                in opcion2.first until opcion2.second -> "No se mejora el artículo"
////                else -> "No option selected"
////            }
//
//        }

    @Suppress("DEPRECATION")
    private fun speak(ttsText: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null)
        }
    }
}
