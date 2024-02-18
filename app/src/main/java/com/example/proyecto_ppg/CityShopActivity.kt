package com.example.proyecto_ppg

import Personaje
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import android.speech.tts.TextToSpeech.OnInitListener

class CityShopActivity :AppCompatActivity(), OnInitListener{

    private lateinit var tts: TextToSpeech
    private lateinit var objPersonaje: Personaje
    private lateinit var barra : UtilBar
    private lateinit var enterButton: Button
    private lateinit var backButton: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_shop)

        initialize()

        enterButton.setOnClickListener {
            val intent = Intent(this,MerchantActivity::class.java)
            intent.putExtra("isCity",true)
            intent.putExtra("personaje",objPersonaje)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            val intent = Intent(this,CityEnterActivity::class.java)
            intent.putExtra("personaje",objPersonaje)
            startActivity(intent)
        }

    }

    @Suppress("DEPRECATION")
    private fun initialize(){
        backButton = findViewById(R.id.backCityShop)
        enterButton = findViewById(R.id.enterCityShop)
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
                    delayAndSpeak(enterButton.text.toString(),2000)
                    delayAndSpeak(backButton.text.toString(),4000)
                    },10000)

            }
        } else {
            Log.e("TTS Error","Error al cargar")
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

}