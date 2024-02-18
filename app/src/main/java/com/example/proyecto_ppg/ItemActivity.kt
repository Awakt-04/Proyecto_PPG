package com.example.proyecto_ppg

import Personaje
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import android.speech.tts.TextToSpeech.OnInitListener
import java.util.Locale

class ItemActivity :AppCompatActivity(), OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var barra: UtilBar
    private lateinit var fondo: ConstraintLayout
    private lateinit var objPersonaje: Personaje
    private lateinit var pickupButton :Button
    private lateinit var continueButton : Button
    private lateinit var itemImage : ImageView
    private lateinit var articulo: Articulo
    private lateinit var dbHelper: DatabaseHelper

    private val listaObjetos = arrayListOf(
        Articulo(Articulo.TipoArticulo.ARMA, Articulo.Nombre.BASTON, 2, 4),
        Articulo(Articulo.TipoArticulo.ARMA, Articulo.Nombre.ESPADA, 2, 4),
        Articulo(Articulo.TipoArticulo.ARMA, Articulo.Nombre.DAGA, 2, 4),
        Articulo(Articulo.TipoArticulo.ARMA, Articulo.Nombre.MARTILLO, 2, 4),
        Articulo(Articulo.TipoArticulo.ARMA, Articulo.Nombre.GARRAS, 2, 4),
        Articulo(Articulo.TipoArticulo.PROTECCION, Articulo.Nombre.ESCUDO, 2, 4),
        Articulo(Articulo.TipoArticulo.PROTECCION, Articulo.Nombre.ARMADURA, 2, 4),
        Articulo(Articulo.TipoArticulo.OBJETO, Articulo.Nombre.POCION, 2, 4),
        Articulo(Articulo.TipoArticulo.OBJETO, Articulo.Nombre.IRA, 2, 4),
        Articulo(Articulo.TipoArticulo.ORO, Articulo.Nombre.MONEDA, 2, 15)
    )

    private var fondoId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        initialize()

        fondo.setBackgroundResource(fondoId)

        do{
            if (dbHelper.getObjetos().isEmpty()) {
                listaObjetos.forEach { _ -> dbHelper.insertarObjetos() } //Para que ingrese los 10 objetos
                Log.d("SQLITE", "Se regenera la bbdd al no quedar objetos")
            }
            articulo = dbHelper.getObjetos().random()
            if (dbHelper.getUnidades(articulo) == 0) {
                Log.d("UNIDADES", "No hay unidades")
                dbHelper.eliminarObjetos(articulo)
                Log.d("UNIDADES", "Se elimina al no existir unidades")
            }
        }while (dbHelper.getUnidades(articulo) == 0) //Si devuelve 0 quiere decir que no existe el articulo, o sea que se ha eliminado
        this.recogerButtonListener(articulo, dbHelper)
        this.continuarButtonListener()
    }

    @Suppress("DEPRECATION")
    private fun initialize(){
        continueButton = findViewById(R.id.backItem)
        dbHelper = DatabaseHelper(this)
        fondo = findViewById(R.id.itemFondo)
        itemImage = findViewById(R.id.itemImage)
        pickupButton = findViewById(R.id.pickUpItem)

        fondoId = intent.getIntExtra("fondo",R.drawable.map_cave)
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

    private fun continuarButtonListener(){
        continueButton.setOnClickListener {
            camino()
        }
    }

    private fun delayAndSpeak(text: String, tiempo: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            speak(text)
        },tiempo)
    }

    @Suppress("DEPRECATION","ResourceAsColor", "InflateParams","MissingInflatedid")
    private fun mostrarObjeto(db : DatabaseHelper, a:Articulo){
        val inf = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inf.inflate(R.layout.pop_up,null)

        val fondo = layout.findViewById<ConstraintLayout>(R.id.FondoPopUP)
        fondo.setBackgroundColor(R.color.negro_transparente)
        val textView = layout.findViewById<TextView>(R.id.textPopUp)
        textView.text = a.toString()
        val imageView = layout.findViewById<ImageView>(R.id.itemPopUp)

        imageView.setImageResource(db.getUrl(a))

        val toast = Toast(applicationContext)
        toast.setGravity(Gravity.CENTER,0,0)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
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
                    delayAndSpeak(itemImage.contentDescription.toString(),2000)
                    delayAndSpeak(pickupButton.text.toString(),4000)
                    delayAndSpeak(continueButton.text.toString(),6000)
                    },10000)

            }
        } else {
            Log.e("TTS Error","Error al cargar")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return barra.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    private fun recogerButtonListener(articulo: Articulo, dbHelper: DatabaseHelper) {
        pickupButton.setOnClickListener {
            val mochilita = objPersonaje.getMochila()
            val articuloAniadido = mochilita.addArticulo(articulo)
            //Leer el comentario de la funcion addMochila() de la clase Mochila para entenderlo mejor
            if (articuloAniadido > 0) {
                Toast.makeText(this, "No hay espacio en la mochila", Toast.LENGTH_SHORT).show()
                return@setOnClickListener //Paramos la función para obligar al usuario que le de al button Continuar
            } else if (articuloAniadido < 0) {
                Toast.makeText(this, "Hubo un error en la base de datos", Toast.LENGTH_SHORT).show()
                Log.d("articuloAniadido", "TipoArticulo y Nombre no admitido")
            }else {
                //Si se añade a la mochila, restamos una unidad del articulo en la bd
                dbHelper.setUnidades(articulo, dbHelper.getUnidades(articulo) - 1)
                mostrarObjeto(dbHelper,articulo)
                Handler(Looper.getMainLooper()).postDelayed({
                    Toast.makeText(this, "Se añadio el artículo correctamente", Toast.LENGTH_SHORT).show()
                }, 3000)
            }
            objPersonaje.setMochila(mochilita)
            Log.d("MochilaPJ", "${objPersonaje.getMochila()}")

            camino()


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