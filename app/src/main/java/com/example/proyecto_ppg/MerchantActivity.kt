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
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import java.util.Locale

class MerchantActivity :AppCompatActivity(), OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var barra : UtilBar
    private lateinit var objPersonaje: Personaje
    //Imagen, 2 botones visibles por defecto
    private lateinit var merchantImage : ImageView
    private lateinit var comerceButton: Button
    private lateinit var continueButton :Button
    //Botones ocultos
    private lateinit var buyButton: Button
    private lateinit var sellButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant)

        initialize()

        comerciarContinuarButtons()
        comprarVenderCancelarButtons()
    }

    @Suppress("DEPRECATION")
    private fun initialize(){
        buyButton = findViewById(R.id.buyButton)
        cancelButton = findViewById(R.id.cancelButton)
        comerceButton = findViewById(R.id.comerceButton)
        continueButton = findViewById(R.id.backShop)
        sellButton = findViewById(R.id.sellButton)
        merchantImage = findViewById(R.id.merchantImage)
        tts = TextToSpeech(this, this)

        objPersonaje = intent.getParcelableExtra("personaje")!!
        barra = UtilBar(this,objPersonaje)
        barra.setupToolbar(this,R.id.barraOpciones)
    }

    private fun comerciarContinuarButtons(){
        comerceButton.setOnClickListener {
            comerceButton.visibility = View.GONE
            continueButton.visibility = View.GONE
            buyButton.visibility = View.VISIBLE
            sellButton.visibility = View.VISIBLE
            cancelButton.visibility = View.VISIBLE
        }

        continueButton.setOnClickListener {
            if(intent.getBooleanExtra("isCity",false)){
                    val intent = Intent(this, CityShopActivity::class.java)
                    intent.putExtra("personaje", objPersonaje)
                    startActivity(intent)
                }
            else{
                val intent = Intent(this, DiceActivity::class.java)
                intent.putExtra("personaje", objPersonaje)
                startActivity(intent)
            }

        }
    }

    private fun comprarVenderCancelarButtons() {
        buyButton.setOnClickListener {
            val intent = Intent(this, MerchantShopActivity::class.java)
            intent.putExtra("personaje", objPersonaje)
            startActivity(intent)
        }

        sellButton.setOnClickListener {
            //Siempre tendrá mínimo un objeto (ORO)
            if (objPersonaje.getMochila().getContenido().size <= 1) {
                Toast.makeText(this, "Que vas a vender si no tienes nada", Toast.LENGTH_SHORT).show()
            }else {
                Log.d("WTFF", "${objPersonaje.getMochila().getContenido().size}" )
                val intent = Intent(this, MerchantSellActivity::class.java)
                intent.putExtra("personaje", objPersonaje)
                startActivity(intent)
            }

            cancelButton.setOnClickListener {
                comerceButton.visibility = View.VISIBLE
                continueButton.visibility = View.VISIBLE
                buyButton.visibility = View.GONE
                sellButton.visibility = View.GONE
                cancelButton.visibility = View.GONE
            }

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
                    delayAndSpeak(merchantImage.contentDescription.toString(),2000)
                    if(comerceButton.isVisible){
                        delayAndSpeak(comerceButton.text.toString(),4000)
                        delayAndSpeak(continueButton.text.toString(),6000)
                    }
                    else{
                        delayAndSpeak(buyButton.text.toString(),4000)
                        delayAndSpeak(sellButton.text.toString(),6000)
                        delayAndSpeak(cancelButton.text.toString(),8000)
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

    @Suppress("DEPRECATION")
    private fun speak(ttsText: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

}
