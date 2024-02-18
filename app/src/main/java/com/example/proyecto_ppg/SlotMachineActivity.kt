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
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class SlotMachineActivity: AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var horizontalScrollView: HorizontalScrollView
    private lateinit var tts: TextToSpeech
    private lateinit var barra : UtilBar
    private lateinit var objPersonaje: Personaje
    private lateinit var texto: TextView
    private lateinit var backButton: Button
    private lateinit var botonRojo: ImageButton
    private val handler = Handler()
    private var isAutoScrolling = false


    private var oroList = listOf<Articulo>(
        Articulo(Articulo.TipoArticulo.ORO, Articulo.Nombre.MONEDA,0, 0, 100),
        Articulo(Articulo.TipoArticulo.ORO, Articulo.Nombre.MONEDA, 0,0, 150),
        Articulo(Articulo.TipoArticulo.ORO, Articulo.Nombre.MONEDA, 0,0, 200),
        Articulo(Articulo.TipoArticulo.ORO, Articulo.Nombre.MONEDA, 0,0, 250),
        Articulo(Articulo.TipoArticulo.ORO, Articulo.Nombre.MONEDA, 0,0, 300),
        Articulo(Articulo.TipoArticulo.ORO, Articulo.Nombre.MONEDA, 0,0, 350),
        Articulo(Articulo.TipoArticulo.ORO, Articulo.Nombre.MONEDA, 0,0, 400),
        Articulo(Articulo.TipoArticulo.ORO, Articulo.Nombre.MONEDA, 0,0, 500),
        Articulo(Articulo.TipoArticulo.ORO, Articulo.Nombre.MONEDA, 0,0, 550),
        Articulo(Articulo.TipoArticulo.ORO, Articulo.Nombre.MONEDA, 0,0, 600)
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slot_machine)

        initialize()
        isAutoScrolling = false

        horizontalScrollView.isHorizontalScrollBarEnabled = false


        horizontalScrollView.setOnTouchListener { _, _ -> true }

        when (intent.getStringExtra("obj")?: "gold") {
            "object" -> {
                texto.text = "Ruleta de objetos"
            }
            "gold" -> {
                texto.text = "Ruleta de oro"
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this,CasinoActivity::class.java)
            intent.putExtra("personaje",objPersonaje)
            startActivity(intent)
        }

        empezarAutoScroll()
        botonRojo.setOnClickListener {
            if (isAutoScrolling) {
                stopAutoScroll()
                val oroRandom = oroList.random()
                Toast.makeText(this, "Has ganado ${oroRandom.getPrecio()}", Toast.LENGTH_SHORT).show()
                objPersonaje.addArticulo(oroRandom)
            }
            else {
                isAutoScrolling = true
                startAutoScroll()
            }
        }



        val linearLayout = findViewById<LinearLayout>(R.id.linearLayoutMio)


        for (i in 0 until 5) {
            val imageView = ImageView(this)
            imageView.setImageResource(R.drawable.obj_oro_moneda) // Reemplaza con tu recurso de imagen
            imageView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            linearLayout.addView(imageView)
        }




    }

    fun empezarAutoScroll() {
        horizontalScrollView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                startAutoScroll()
                horizontalScrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun stopAutoScroll() {
        isAutoScrolling = false
        handler.removeCallbacksAndMessages(null)
    }


    private fun startAutoScroll() {
        val scrollSpeed = 100
        val scrollDelay = 100

        handler.postDelayed(object : Runnable {
            override fun run() {
                val currentScrollX = horizontalScrollView.scrollX
                val maxScrollX = horizontalScrollView.getChildAt(0).width - horizontalScrollView.width

                val newScrollX = if (currentScrollX + scrollSpeed > maxScrollX) 0 else currentScrollX + scrollSpeed

                horizontalScrollView.smoothScrollTo(newScrollX, 0)

                if (isAutoScrolling) {
                    handler.postDelayed(this, scrollDelay.toLong())
                }
            }
        }, scrollDelay.toLong())
    }



    @Suppress("DEPRECATION")
    private fun initialize(){
        botonRojo = findViewById(R.id.pulsarBtn)
        horizontalScrollView = findViewById(R.id.horizontalScrollView)

        backButton = findViewById(R.id.backSlotButton)
        texto = findViewById(R.id.slotText)
        tts = TextToSpeech(this, this)

        objPersonaje = intent.getParcelableExtra("personaje")?: Personaje("Jorge", Personaje.Raza.Humano, Personaje.Clase.Brujo, Personaje.EstadoVital.Adulto)
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
                    delayAndSpeak(texto.text.toString(),2000)
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