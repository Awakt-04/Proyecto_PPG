package com.example.proyecto_ppg

import Personaje
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class ShowCharacterActivity : AppCompatActivity() {
    private lateinit var objPersonaje: Personaje
    private lateinit var iconImage :ImageView
    private lateinit var textView :TextView
    private lateinit var backButton :Button
    private lateinit var continueButton :Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_character)

        initialize()

        textView.text = objPersonaje.toString()

        cambioImagen()

        continueButton.setOnClickListener {
            val intent = Intent(this, DiceActivity::class.java)
            objPersonaje.addArticulo(Articulo(Articulo.TipoArticulo.ORO, Articulo.Nombre.MONEDA, 0, 0, 0))
            val db = DatabaseHelper(this)
            db.insertarPersonaje(objPersonaje)
            intent.putExtra("personaje", objPersonaje)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            startActivity(Intent(this, CreationActivity::class.java))
        }
    }

    @Suppress("DEPRECATION")
    private fun initialize(){
        backButton = findViewById(R.id.backCreation)
        continueButton = findViewById(R.id.startButton)
        iconImage = findViewById(R.id.iconImageView)
        textView = findViewById(R.id.characterTextView)

        objPersonaje = intent.getParcelableExtra("personaje")!!
    }

    private fun cambioImagen() {
        when (objPersonaje.getClase()) {
            Personaje.Clase.Brujo -> iconImage.setBackgroundResource(R.drawable.class_brujo_marco)
            Personaje.Clase.Mago -> iconImage.setBackgroundResource(R.drawable.class_mago_marco)
            Personaje.Clase.Guerrero -> iconImage.setBackgroundResource(R.drawable.class_guerrero_marco)
        }

        when (objPersonaje.getRaza()) {
            Personaje.Raza.Humano ->
                when (objPersonaje.getEstadoVital()) {
                    Personaje.EstadoVital.Anciano -> iconImage.setImageResource(R.drawable.icon_humano_anciano)
                    Personaje.EstadoVital.Joven -> iconImage.setImageResource(R.drawable.icon_humano_joven)
                    Personaje.EstadoVital.Adulto -> iconImage.setImageResource(R.drawable.icon_humano_adulto)
                }

            Personaje.Raza.Elfo ->
                when (objPersonaje.getEstadoVital()) {
                    Personaje.EstadoVital.Anciano -> iconImage.setImageResource(R.drawable.icon_elfo_anciano)
                    Personaje.EstadoVital.Joven -> iconImage.setImageResource(R.drawable.icon_elfo_joven)
                    Personaje.EstadoVital.Adulto -> iconImage.setImageResource(R.drawable.icon_elfo_adulto)
                }

            Personaje.Raza.Enano ->
                when (objPersonaje.getEstadoVital()) {
                    Personaje.EstadoVital.Anciano -> iconImage.setImageResource(R.drawable.icon_enano_anciano)
                    Personaje.EstadoVital.Joven -> iconImage.setImageResource(R.drawable.icon_enano_joven)
                    Personaje.EstadoVital.Adulto -> iconImage.setImageResource(R.drawable.icon_elfo_adulto)
                }

            Personaje.Raza.Maldito ->
                when (objPersonaje.getEstadoVital()) {
                    Personaje.EstadoVital.Anciano -> iconImage.setImageResource(R.drawable.icon_maldito_anciano)
                    Personaje.EstadoVital.Joven -> iconImage.setImageResource(R.drawable.icon_maldito_joven)
                    Personaje.EstadoVital.Adulto -> iconImage.setImageResource(R.drawable.icon_maldito_adulto)
                }
        }
    }
}