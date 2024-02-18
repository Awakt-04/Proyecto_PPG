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

class EnemyActivity :AppCompatActivity(),OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var fondo: ConstraintLayout
    private lateinit var barra : UtilBar
    private lateinit var objPersonaje: Personaje
    private lateinit var fightButton : Button
    private lateinit var fleeButton : Button
    private lateinit var enemyImage : ImageView
    private var imgId = 0
    private var fondoId = 0
    private var flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enemy)

        initialize()

        fondo.setBackgroundResource(fondoId)
        enemyImage.setImageResource(imgId)

        fightButton.setOnClickListener {
            val intent = Intent(this,EnemyFightActivity::class.java)
            intent.putExtra("personaje",objPersonaje)
            intent.putExtra("isAlley",flag)
            intent.putExtra("imgId",imgId)
            intent.putExtra("fondo",fondoId)
            startActivity(intent)
        }
        fleeButton.setOnClickListener {
            camino()
        }
    }

    @Suppress("DEPRECATION")
    private fun initialize(){
        enemyImage = findViewById(R.id.enemyImage)
        fightButton = findViewById(R.id.fightButton)
        fleeButton = findViewById(R.id.fleeButton)
        fondo = findViewById(R.id.fondoEnemy)
        tts = TextToSpeech(this, this)

        fondoId = intent.getIntExtra("fondo",R.drawable.map_cave)
        flag = intent.getBooleanExtra("isAlley",false)
        imgId = intent.getIntExtra("imgId",randEnemy())

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
                    delayAndSpeak(enemyImage.contentDescription.toString(),2000)
                    delayAndSpeak(fightButton.text.toString(),4000)
                    delayAndSpeak(fleeButton.text.toString(),6000)
                },10000)

            }
        } else {
            Log.e("TTS Error","Error al cargar")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return barra.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    private fun randEnemy(): Int {
        return when ((1..4).random()) {
            1 -> {
                R.drawable.enemy_dodoria
            }

            2 -> {
                R.drawable.enemy_puipui
            }

            3 -> {
                R.drawable.enemy_yakon
            }

            else -> {
                R.drawable.enemy_zarbon
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