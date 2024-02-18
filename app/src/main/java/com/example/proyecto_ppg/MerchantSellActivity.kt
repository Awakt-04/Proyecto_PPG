package com.example.proyecto_ppg

import Personaje
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MerchantSellActivity : AppCompatActivity() {

    private lateinit var tts: TextToSpeech
    private lateinit var barra : UtilBar

    private lateinit var imgObjeto: ImageView
    private lateinit var unidadesDeseadas: TextView
    private lateinit var leftObjeto: ImageButton
    private lateinit var rightObjeto: ImageButton

    private lateinit var unidadesDisponiblesText: TextView
    private lateinit var leftUnidadesButton: ImageButton
    private lateinit var rightUnidadesButton: ImageButton


    private lateinit var sellButton: Button
    private lateinit var backButton: Button

    private lateinit var objPersonaje : Personaje
    private lateinit var articulo: Articulo




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant_sell)
        initialize()

        sliderObjeto()
        buyButtonListener()
        sliderUnidadesListener()

    }

    @Suppress("DEPRECATION")
    private fun initialize(){
        backButton = findViewById(R.id.backCreation)
        sellButton = findViewById(R.id.button)
        imgObjeto = findViewById(R.id.itemShopImage)
        leftObjeto = findViewById(R.id.left)
        leftUnidadesButton = findViewById(R.id.minus)
        rightObjeto = findViewById(R.id.right)
        rightUnidadesButton = findViewById(R.id.plus)
        unidadesDeseadas = findViewById(R.id.ItemShopText)
        unidadesDisponiblesText = findViewById(R.id.itemCount)

        objPersonaje = intent.getParcelableExtra("personaje")!!
        barra = UtilBar(this,objPersonaje)
        barra.setupToolbar(this,R.id.barraOpciones)

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return barra.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        barra.onCreateOptionsMenu(menu)
        return true
    }

    private fun buyButtonListener() {
        sellButton.setOnClickListener {
            if (articulo.getTipoArticulo() == Articulo.TipoArticulo.ORO) {
                Toast.makeText(this, "No puedes vender ORO", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val unidadesDisponibles = objPersonaje.getMochila().getContenido().count { articulo == it } //Articulo es asignado en sliderObjeto
            val unidadesAVender = unidadesDeseadas.text.toString().toInt()

            if(unidadesAVender <= unidadesDisponibles) {
                for (i in 1..unidadesAVender)
                    objPersonaje.delArticulo(articulo)

                unidadesDisponiblesText.text =
                    (unidadesDisponiblesText.text.toString().toInt() - unidadesAVender).toString()
                Toast.makeText(this, "Tu venta ha sido exitosa", Toast.LENGTH_SHORT).show()
            }else
                Toast.makeText(this, "No tienes suficientes unidades para vender", Toast.LENGTH_SHORT).show()
        }


        backButton.setOnClickListener {
            val intent = Intent(this, MerchantActivity::class.java)
            intent.putExtra("personaje", objPersonaje)
            startActivity(intent)
        }

    }

    private fun sliderObjeto(){
        val db = DatabaseHelper(this)
        val inventario = objPersonaje.getMochila().getContenido()
        val inventarioHs = objPersonaje.getMochila().getContenido().toHashSet()

        articulo = inventarioHs.elementAt(0)
        imgObjeto.setImageResource(db.getUrl(articulo))
        unidadesDisponiblesText.text = inventario.count { articulo == it }.toString()
        var pos = 0


        leftObjeto.setOnClickListener {
            if (inventarioHs.indexOf(articulo) > 0){
                articulo = inventarioHs.elementAt(--pos)
            }else{
                pos = inventarioHs.size - 1
                articulo = inventarioHs.elementAt(pos)
            }
            imgObjeto.setImageResource(db.getUrl(articulo))
            unidadesDisponiblesText.text = inventario.count { articulo == it }.toString()
        }


        rightObjeto.setOnClickListener {
            if (inventarioHs.indexOf(articulo) < (inventarioHs.size - 1)){
                articulo = inventarioHs.elementAt(++pos)
            }else{
                pos = 0
                articulo = inventarioHs.elementAt(pos)
            }
            imgObjeto.setImageResource(db.getUrl(articulo))
            unidadesDisponiblesText.text = inventario.count { articulo == it }.toString()
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

}