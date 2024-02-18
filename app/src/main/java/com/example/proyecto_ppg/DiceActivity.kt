package com.example.proyecto_ppg

import Personaje
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.Locale

class DiceActivity : AppCompatActivity(), OnInitListener{

    private lateinit var barra : UtilBar
    private lateinit var objPersonaje: Personaje
    private lateinit var dice : ImageButton
    private lateinit var diceInfo: TextView
    private lateinit var scenario : ConstraintLayout
    private lateinit var tts: TextToSpeech

    private var fondo = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dice)

        initialize()



        dice.setOnClickListener{
            aventura()
        }

        fondo = intent.getIntExtra("fondo",cambioFondo())
        scenario.setBackgroundResource(fondo)
    }
    @Suppress("DEPRECATION")
    private fun initialize(){
        dice = findViewById(R.id.diceButton)
        diceInfo = findViewById(R.id.diceInfo)
        scenario = findViewById(R.id.fondoDado)
        tts = TextToSpeech(this,this)

        objPersonaje = intent.getParcelableExtra("personaje")!!

        barra = UtilBar(this,objPersonaje)
        barra.setupToolbar(this,R.id.barraOpciones)
    }

    private fun aventura(){
        when (rollDice()){
            1,
            2 -> {
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, EnemyActivity::class.java)
                    intent.putExtra("personaje", objPersonaje)
                    intent.putExtra("fondo",fondo)
                    startActivity(intent)},
                    1000)

            }
            3 ->Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MerchantActivity::class.java)
                intent.putExtra("personaje", objPersonaje)
                startActivity(intent)},
                1000)
            4 ->  Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, CityActivity::class.java)
                intent.putExtra("personaje", objPersonaje)
                startActivity(intent)},
                1000)
            5,
            6 -> Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, ItemActivity::class.java)
                intent.putExtra("personaje", objPersonaje)
                intent.putExtra("fondo",fondo)
                startActivity(intent)},
                1000)
        }
    }

    private fun cambioFondo(): Int{
        return when ((1..4).random()) {
            1 -> R.drawable.map_cave
            2 -> R.drawable.map_city
            3 -> R.drawable.map_field
            else -> R.drawable.map_mountains
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
                    delayAndSpeak(dice.contentDescription.toString(),2000)
                    delayAndSpeak(diceInfo.text.toString(),4000)
                },10000)

            }
        } else {
            Log.e("TTS Error","Error al cargar")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return barra.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    private fun rollDice() :Int {
        // variable para obtener una cara aleatoria del dado
        val randomFace = (1..6).random()
        val rotationDegree = 360f * (1..6).random() // Rota de 1 a 6 veces de forma aleatoria

        // creamos una variable para poder realizar la animación entre 0 y las vueltas obtenidas con la variable anterior
        val rotateAnimation = RotateAnimation(
            0f, rotationDegree,
            // estos valores hacen referencia a la propia animación y los valores de X e Y son 0.5 para que rote sobre sí mismo
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        // hacemos que rote durante un segundo e iniciamos la animación
        rotateAnimation.duration = 1000
        dice.startAnimation(rotateAnimation)

        // Se utiliza un handler para poder actualizar la imagen del dado con un delay de medio segundo
        Handler(Looper.getMainLooper()).postDelayed({
            updateDiceImage(randomFace)
        }, 500)

        return randomFace
    }

    @Suppress("DEPRECATION")
    private fun speak(ttsText: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    // función para actualizar la imagen del dado
    private fun updateDiceImage(face: Int) {
        val drawableId = when (face) {
            1 -> R.drawable.dice1
            2 -> R.drawable.dice2
            3 -> R.drawable.dice3
            4 -> R.drawable.dice4
            5 -> R.drawable.dice5
            else -> R.drawable.dice6
        }

        dice.setImageResource(drawableId)
    }

}