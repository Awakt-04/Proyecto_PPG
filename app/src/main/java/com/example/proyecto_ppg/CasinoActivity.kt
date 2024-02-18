package com.example.proyecto_ppg

import Personaje
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import java.util.Locale

class CasinoActivity: AppCompatActivity(), OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var barra : UtilBar
    private lateinit var botones: Array<Button>
    private lateinit var objPersonaje: Personaje

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_casino)

        initialize()

        opcionesBotones()
    }

    @Suppress("DEPRECATION")
    private fun initialize(){
        botones = arrayOf(
            findViewById(R.id.boxButton),
            findViewById(R.id.backCasino),
            findViewById(R.id.goldBoxCasino),
            findViewById(R.id.objectBoxCasino),
            findViewById(R.id.upgradeCasino),
            findViewById(R.id.cancelCasinoButton)
        )
        tts = TextToSpeech(this, this)

        objPersonaje = intent.getParcelableExtra("personaje")!!

        barra = UtilBar(this,objPersonaje)
        barra.setupToolbar(this,R.id.barraOpciones)
    }

    private fun delayAndSpeak(text: String, tiempo: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            speak(text)
        },tiempo)
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
                    if(botones[0].isVisible) {
                        delayAndSpeak(botones[0].text.toString(), 2000)
                        delayAndSpeak(botones[1].text.toString(), 4000)
                    } else {
                        delayAndSpeak(botones[2].text.toString(), 2000)
                        delayAndSpeak(botones[3].text.toString(), 4000)
                        delayAndSpeak(botones[4].text.toString(), 6000)
                        delayAndSpeak(botones[5].text.toString(), 8000)
                    }
                },10000)

            }
        } else {
            Log.e("TTS Error","Error al cargar")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return barra.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    private fun opcionesBotones(){
        for (boton in botones){
            boton.setOnClickListener{ btn ->
                when (btn.id) {
                    R.id.boxButton -> {
                        botones[0].visibility = View.GONE
                        botones[1].visibility = View.GONE

                        botones[2].visibility = View.VISIBLE
                        botones[3].visibility = View.VISIBLE
                        botones[4].visibility = View.VISIBLE
                        botones[5].visibility = View.VISIBLE
                    }
                    R.id.backCasino -> {
                        val intent = Intent(this,CityCasinoActivity::class.java)
                        intent.putExtra("personaje",objPersonaje)
                        startActivity(intent)
                    }
                    R.id.goldBoxCasino -> {
                        val intent = Intent(this,SlotMachineActivity::class.java)
                        intent.putExtra("personaje",objPersonaje)
                        intent.putExtra("obj","gold")
                        startActivity(intent)
                    }
                    R.id.objectBoxCasino -> {
                        val intent = Intent(this,SlotMachineActivity::class.java)
                        intent.putExtra("personaje",objPersonaje)
                        intent.putExtra("obj","object")
                        startActivity(intent)
                    }
                    R.id.upgradeCasino -> {
                        val intent = Intent(this,UpgradeCasinoActivity::class.java)
                        intent.putExtra("personaje",objPersonaje)
                        startActivity(intent)
                    }
                    R.id.cancelCasinoButton -> {
                        botones[0].visibility = View.VISIBLE
                        botones[1].visibility = View.VISIBLE

                        botones[2].visibility = View.GONE
                        botones[3].visibility = View.GONE
                        botones[4].visibility = View.GONE
                        botones[5].visibility = View.GONE
                    }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun speak(ttsText: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null)
        }
    }
}