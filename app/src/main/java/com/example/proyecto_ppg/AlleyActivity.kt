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
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.Locale
import android.speech.tts.TextToSpeech.OnInitListener

class AlleyActivity :AppCompatActivity(),OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var barra : UtilBar
    private lateinit var objPersonaje: Personaje
    private lateinit var volverButton: Button
    private lateinit var rebuscarButton: Button
    private lateinit var fondo :ConstraintLayout
    private var esDia = true
    private var fondoId = 0
    private lateinit var cambioFondoHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alley)

        initialize()

        cambioFondoHandler.post(
            object: Runnable{
                override fun run() {
                    cambioFondo()
                    cambioFondoHandler.postDelayed(this, 10000
                    )
                }
            })

        rebuscarButton.setOnClickListener{
            rebuscarObjeto()
        }

        volverButton.setOnClickListener{
            val intent = Intent(this,CityEnterActivity::class.java)
            intent.putExtra("dia",!esDia)
            intent.putExtra("personaje",objPersonaje)
            startActivity(intent)
        }

    }

    @Suppress("DEPRECATION")
    private fun initialize(){
        cambioFondoHandler = Handler()
        fondo = findViewById(R.id.fondoCallejon)
        rebuscarButton = findViewById(R.id.pickUpAlley)
        volverButton = findViewById(R.id.backAlley)
        tts = TextToSpeech(this, this)

        esDia = intent.getBooleanExtra("dia",true)
        objPersonaje = intent.getParcelableExtra("personaje")!!

        barra = UtilBar(this,objPersonaje)
        barra.setupToolbar(this,R.id.barraOpciones)
    }

    private fun cambioFondo() {
        esDia = if(esDia) {
            fondoId = R.drawable.map_city_alley
            fondo.setBackgroundResource(fondoId)
            false
        } else {
            fondoId = R.drawable.map_city_alley_night
            fondo.setBackgroundResource(fondoId)
            true
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
                    delayAndSpeak(rebuscarButton.text.toString(),2000)
                    delayAndSpeak(volverButton.text.toString(),4000)
                },10000)

            }
        } else {
            Log.e("TTS Error","Error al cargar")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return barra.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    private fun rebuscarObjeto(){

        if (fondoId == 0)
            fondoId = R.drawable.map_city_alley

        if(esDia)
            when ((0..9).random()) {
                0,4,8 -> {
                    Toast.makeText(this,"¡Has encontrado un enemigo!",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,EnemyActivity::class.java)
                    intent.putExtra("personaje",objPersonaje)
                    intent.putExtra("isAlley",true)
                    intent.putExtra("fondo",fondoId)
                    startActivity(intent)
                }
                1,5,7 -> {
                    Toast.makeText(this,"No has encontrado nada",Toast.LENGTH_SHORT).show()
                }
                2,3,6,9 -> {
                    Toast.makeText(this,"¡Has encontrado un objeto!",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,ItemActivity::class.java)
                    intent.putExtra("personaje",objPersonaje)
                    intent.putExtra("isAlley",true)
                    startActivity(intent)
                }

            }
        else
            when ((0..9).random()) {
                0,2,4,6,8 -> {
                    Toast.makeText(this,"¡Has encontrado un enemigo!",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,EnemyActivity::class.java)
                    intent.putExtra("personaje",objPersonaje)
                    intent.putExtra("isAlley",true)
                    startActivity(intent)
                }
                1,9 -> {
                    Toast.makeText(this,"¡Has encontrado un objeto!",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,ItemActivity::class.java)
                    intent.putExtra("personaje",objPersonaje)
                    intent.putExtra("isAlley",true)
                    startActivity(intent)
                }
                3,5,7 -> {
                    Toast.makeText(this,"No has encontrado nada",Toast.LENGTH_SHORT).show()
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
