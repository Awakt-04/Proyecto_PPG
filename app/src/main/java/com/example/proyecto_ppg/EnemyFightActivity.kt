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
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.Locale

class EnemyFightActivity :AppCompatActivity(), OnInitListener{

    private lateinit var tts: TextToSpeech
    private lateinit var fondo: ConstraintLayout
    private lateinit var barra : UtilBar
    private lateinit var objPersonaje: Personaje
    private lateinit var img :ImageView
    private lateinit var button :Button
    private var idImagen = 0
    private var fondoId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enemy_fight)

        initialize()

        fondo.setBackgroundResource(fondoId)
        img.setImageResource(idImagen)

        button.setOnClickListener {
            camino()
        }
    }

    @Suppress("DEPRECATION")
    private fun initialize(){
        button = findViewById(R.id.backEnemyFight)
        fondo = findViewById(R.id.fondoEnemyFight)
        fondo.setBackgroundResource(fondoId)
        img = findViewById(R.id.enemyFightImage)
        tts = TextToSpeech(this, this)

        fondoId = intent.getIntExtra("fondo",R.drawable.map_cave)
        idImagen = intent.getIntExtra("imgId", R.drawable.enemy_zarbon)
        objPersonaje = intent.getParcelableExtra("personaje")!!
        barra = UtilBar(this,objPersonaje)
        barra.setupToolbar(this,R.id.barraOpciones)
    }

    private fun camino(){
        if(intent.getBooleanExtra("isAlley",false)) {
            val intent = Intent(this, AlleyActivity::class.java)
            intent.putExtra("personaje", objPersonaje)
            startActivity(intent)
        }
        else {
            val intent = Intent(this, DiceActivity::class.java)
            intent.putExtra("fondo",fondoId)
            intent.putExtra("personaje", objPersonaje)
            startActivity(intent)
        }
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
                    delayAndSpeak(img.contentDescription.toString(),2000)
                    delayAndSpeak(button.text.toString(),4000)
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
