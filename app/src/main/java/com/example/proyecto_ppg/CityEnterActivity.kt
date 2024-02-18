package com.example.proyecto_ppg

import Personaje
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.Locale
import android.speech.tts.TextToSpeech.OnInitListener
import android.view.MenuItem
import android.widget.ImageButton

class CityEnterActivity :AppCompatActivity(), OnInitListener {

    private lateinit var adapter: ImageAdapter
    private lateinit var cambioFondoHandler: Handler
    private var esDia: Boolean = true
    private lateinit var objPersonaje: Personaje
    private lateinit var tts: TextToSpeech

    private lateinit var barra : UtilBar
    private lateinit var botones: GridView
    private lateinit var chat: ImageButton
    private lateinit var fondo :ConstraintLayout

    private var list = intArrayOf(
        R.drawable.map_city_alley,R.drawable.map_city_shop,
        R.drawable.map_city_casino,R.drawable.map_city)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_enter)

        initialize()

        cambioFondoHandler.post(
            object: Runnable{
                override fun run() {
                    cambioFondo()
                    cambioFondoHandler.postDelayed(this, 10000
                    )
                }
            })

        adaptarImagen()

        chat.setOnClickListener {
            val intent = Intent(this,ChatBotActivity::class.java)
            intent.putExtra("personaje",objPersonaje)
            startActivity(intent)
        }

    }
    @Suppress("DEPRECATION")
    private fun initialize(){
        adapter = ImageAdapter(this,list,450)
        botones = findViewById(R.id.opciones)
        cambioFondoHandler = Handler()
        chat = findViewById(R.id.chatButton)
        fondo = findViewById(R.id.fondoCityEnter)
        tts = TextToSpeech(this, this)

        esDia = intent.getBooleanExtra("dia",true)
        objPersonaje = intent.getParcelableExtra("personaje")!!
        barra = UtilBar(this,objPersonaje)
        barra.setupToolbar(this,R.id.barraOpciones)
    }

    private fun adaptarImagen() {
        var item :Int

        adapter  = ImageAdapter(this,list,450)

        botones.adapter = adapter

        botones.setOnItemClickListener { _, _, position, _ ->
            item = list[position]
            try {
                Toast.makeText(this,resources.getResourceEntryName(item), Toast.LENGTH_SHORT).show()
                when(list[position]) {
                    R.drawable.map_city_alley_night, R.drawable.map_city_alley -> {
                        intent = Intent(this, AlleyActivity::class.java)
                        intent.putExtra("personaje",objPersonaje)
                        intent.putExtra("dia",!esDia)
                        startActivity(intent)
                    }
                    R.drawable.map_city_shop -> {
                        intent = Intent(this,CityShopActivity::class.java)
                        intent.putExtra("personaje",objPersonaje)
                        intent.putExtra("isCity",true)
                        startActivity(intent)
                    }
                    R.drawable.map_city_casino -> {
                        intent = Intent(this,CityCasinoActivity::class.java)
                        intent.putExtra("personaje",objPersonaje)
                        startActivity(intent)
                    }
                    R.drawable.map_city -> {
                        intent = Intent(this,CityActivity::class.java)
                        intent.putExtra("personaje",objPersonaje)
                        startActivity(intent)
                    }
                }
            }catch (e : Resources.NotFoundException){ e.printStackTrace() }
        }
    }

    private fun cambioFondo() {

        if (esDia) {
            fondo.setBackgroundResource(R.drawable.map_city_enter)
            list[0] = R.drawable.map_city_alley
        } else {
            fondo.setBackgroundResource(R.drawable.map_city_enter_night)
            list[0] = R.drawable.map_city_alley_night
        }

        esDia = !esDia
        adapter.notifyDataSetChanged()
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
                    delayAndSpeak(botones.contentDescription.toString(),2000)
                    delayAndSpeak(chat.contentDescription.toString(),4000)
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