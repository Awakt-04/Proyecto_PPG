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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

open class MerchantShopActivity :AppCompatActivity(), OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var barra : UtilBar
    private lateinit var imgObjeto: ImageView
    private lateinit var unidadesDeseadas: TextView
    private lateinit var leftObjeto: ImageButton
    private lateinit var rightObjeto: ImageButton
    private lateinit var articulo: Articulo
    private lateinit var backButton: Button
    private lateinit var objPersonaje : Personaje
    private lateinit var unidadesDisponiblesText: TextView
    private lateinit var leftUnidadesButton: ImageButton
    private lateinit var rightUnidadesButton: ImageButton

    private lateinit var buyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant_shop)

        initialize()

        sliderObjeto()
        buyButtonListener()
        sliderUnidadesListener()
    }

    @Suppress("DEPRECATION")
    private fun initialize(){
        backButton = findViewById(R.id.backCreation)
        buyButton = findViewById(R.id.button)
        imgObjeto = findViewById(R.id.itemShopImage)
        leftObjeto = findViewById(R.id.left)
        leftUnidadesButton = findViewById(R.id.minus)
        rightObjeto = findViewById(R.id.right)
        rightUnidadesButton = findViewById(R.id.plus)
        unidadesDeseadas = findViewById(R.id.ItemShopText)
        unidadesDisponiblesText = findViewById(R.id.itemCount)
        tts = TextToSpeech(this, this)

        objPersonaje = intent.getParcelableExtra("personaje")!!
        barra = UtilBar(this,objPersonaje)
        barra.setupToolbar(this,R.id.barraOpciones)

    }

    private fun buyButtonListener() {
        buyButton.setOnClickListener {
            val dbHelper = DatabaseHelper(this)
            val unidadesDisponibles = dbHelper.getUnidades(articulo) //Articulo es asignado en sliderObjeto
            val unidadesAComprar = unidadesDeseadas.text.toString().toInt()
            val pesoTotalArt =  unidadesAComprar * articulo.getPeso()


            if (unidadesAComprar <= unidadesDisponibles){
                val oroDelPJ = objPersonaje.getMochila().getContenido().find { it.getTipoArticulo() == Articulo.TipoArticulo.ORO }!!.getPrecio()
                val precioTotalCompra = articulo.getPrecio() * unidadesAComprar
                if (oroDelPJ >= precioTotalCompra){
                    val pesoLibreMochila = objPersonaje.getMochila().getPesoLibreMochila()
                    if(pesoTotalArt <= pesoLibreMochila){

                        for (i in 1..unidadesDeseadas.text.toString().toInt())
                            objPersonaje.addArticulo(articulo)
                        objPersonaje.delArticulo(Articulo(Articulo.TipoArticulo.ORO, Articulo.Nombre.MONEDA, 0,0, precioTotalCompra))
                        dbHelper.setUnidades(articulo, unidadesDisponibles - unidadesAComprar)

                        Toast.makeText(this, "Has comprado $unidadesAComprar unidades de ${articulo.getNombre()}", Toast.LENGTH_SHORT).show()
                        unidadesDisponiblesText.text = (unidadesDisponiblesText.text.toString().toInt() - unidadesAComprar).toString()
                    }else
                        Toast.makeText(this, "No hay espacio suficiente en la mochila", Toast.LENGTH_SHORT).show()
                }else
                    Toast.makeText(this, "No tienes suficiente oro", Toast.LENGTH_SHORT).show()

            }else
                Toast.makeText(this, "Unidades no disponibles", Toast.LENGTH_SHORT).show()

        }

        backButton.setOnClickListener {
            val intent = Intent(this, MerchantActivity::class.java)
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
                    delayAndSpeak(imgObjeto.contentDescription.toString(),2000)
                    delayAndSpeak(leftUnidadesButton.contentDescription.toString(),4000)
                    delayAndSpeak(unidadesDeseadas.text.toString(),6000)
                    delayAndSpeak(rightUnidadesButton.contentDescription.toString(),8000)
                    delayAndSpeak(leftObjeto.contentDescription.toString(),10000)
                    delayAndSpeak(unidadesDisponiblesText.text.toString(),12000)
                    delayAndSpeak(rightObjeto.contentDescription.toString(),14000)
                    delayAndSpeak(buyButton.text.toString(),16000)
                    delayAndSpeak(backButton.text.toString(),18000)
                },10000)

            }
        } else {
            Log.e("TTS Error","Error al cargar")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return barra.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    private fun sliderObjeto(){
        val db = DatabaseHelper(this)
        val inventario = db.getObjetos()
        articulo = inventario.elementAt(0)
        imgObjeto.setImageResource(db.getUrl(articulo))
        unidadesDisponiblesText.text = db.getUnidades(articulo).toString()
        var pos = 0


        leftObjeto.setOnClickListener {
            if (inventario.indexOf(articulo) > 0){
                articulo = inventario.elementAt(--pos)
            }else{
                pos = inventario.size - 1
                articulo = inventario.elementAt(pos)
            }
            imgObjeto.setImageResource(db.getUrl(articulo))
            unidadesDisponiblesText.text = db.getUnidades(articulo).toString()
        }


        rightObjeto.setOnClickListener {
            if (inventario.indexOf(articulo) < (inventario.size - 1)){
                articulo = inventario.elementAt(++pos)
            }else{
                pos = 0
                articulo = inventario.elementAt(pos)
            }
            imgObjeto.setImageResource(db.getUrl(articulo))
            unidadesDisponiblesText.text = db.getUnidades(articulo).toString()
        }

    }

    private fun sliderUnidadesListener() {
        unidadesDeseadas.text = "1"
        leftUnidadesButton.setOnClickListener {
            var unidades = unidadesDeseadas.text.toString().toInt()
            if (unidades >= 2)
                unidadesDeseadas.text = (--unidades).toString()
        }

        rightUnidadesButton.setOnClickListener {
            val unidades = unidadesDeseadas.text.toString().toInt()
            unidadesDeseadas.text = (unidades.plus(1)).toString()
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
