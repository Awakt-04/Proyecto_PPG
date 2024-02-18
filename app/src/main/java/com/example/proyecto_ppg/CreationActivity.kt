package com.example.proyecto_ppg

import Personaje
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.annotation.RequiresApi

class CreationActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var  raceSpinner: Spinner
    private lateinit var  classSpinner: Spinner
    private lateinit var  vitalStatusSpinner: Spinner
    private lateinit var characterImage: ImageView
    private lateinit var classImage: ImageView
    private lateinit var creationButton :Button

    // Variables(propiedades) para el constructor personaje
    private lateinit var pjNombre: String
    private lateinit var pjRaza: Personaje.Raza
    private lateinit var pjClase: Personaje.Clase
    private lateinit var pjEstadoVital: Personaje.EstadoVital

    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creation)

        initialize()

        this.botonListener()
        this.cambiarImgPersonaje()
        this.cambiarImgClase()
    }

    private fun initialize(){
        characterImage = findViewById(R.id.characterImageView)
        classImage = findViewById(R.id.classImageView)
        classSpinner = findViewById(R.id.classSpiner)
        editTextName = findViewById(R.id.nameEditText)
        creationButton = findViewById(R.id.creationButton)
        raceSpinner = findViewById(R.id.raceSpinner)
        vitalStatusSpinner = findViewById(R.id.vitalStatusSpinner)

        uid = intent.getStringExtra("uid")!!
    }

    //Cuando Clickemos el button
    private fun botonListener(){
        creationButton.setOnClickListener {
            val intent = Intent(this, ShowCharacterActivity::class.java)
            pjNombre = editTextName.text.toString()
            val objPersonaje = Personaje(pjNombre, pjRaza, pjClase, pjEstadoVital)
            objPersonaje.setUid(uid)
            intent.putExtra("personaje",objPersonaje)
            startActivity(intent)
        }
    }

    //Procedimiento que cambia la imagenClase segun el SpinnerClase
    private fun cambiarImgClase(){
        val adapterClase = ArrayAdapter.createFromResource(
            this, R.array.Classes,
            android.R.layout.simple_spinner_item
        )
        adapterClase.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        classSpinner.adapter = adapterClase
        classSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        classImage.setImageResource(R.drawable.class_brujo_rombo)
                        creationButton.setBackgroundColor(R.color.background_brujo)

                    }
                    1 -> {
                        classImage.setImageResource(R.drawable.class_guerrero_rombo)
                        creationButton.setBackgroundColor(R.color.background_guerrero)
                    }
                    2 -> {
                        classImage.setImageResource(R.drawable.class_mago_rombo)
                        creationButton.setBackgroundColor(R.color.background_mago)
                    }
                }
                //Extraemos la string de la spinner en la propiedad claseP
                pjClase = Personaje.Clase.valueOf(parent?.getItemAtPosition(position).toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    //Procedimiento que cambiar imgPersonaje segun spinnerRaza y spinnerEstado
    private fun cambiarImgPersonaje() {
        val adapterRaza = ArrayAdapter.createFromResource(
            this, R.array.Races,
            android.R.layout.simple_spinner_item
        )
        adapterRaza.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        raceSpinner.adapter = adapterRaza

        raceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentRaza: AdapterView<*>?,
                view: View?,
                positionRaza: Int,
                id: Long
            ) {
                val adapterEstado = ArrayAdapter.createFromResource(
                    this@CreationActivity, R.array.VitalStatus2,
                    android.R.layout.simple_spinner_item
                )
                adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                vitalStatusSpinner.adapter = adapterEstado

                vitalStatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        // Cambiar las imágenes según las selecciones de Spinners
                        when (positionRaza to position) {
                            0 to 0 -> characterImage.setImageResource(R.drawable.sprite_humano_anciano)
                            0 to 1 -> characterImage.setImageResource(R.drawable.sprite_humano_adulto)
                            0 to 2 -> characterImage.setImageResource(R.drawable.sprite_humano_joven)

                            1 to 0 -> characterImage.setImageResource(R.drawable.sprite_elfo_anciano)
                            1 to 1 -> characterImage.setImageResource(R.drawable.sprite_elfo_adulto)
                            1 to 2 -> characterImage.setImageResource(R.drawable.sprite_elfo_joven)

                            2 to 0 -> characterImage.setImageResource(R.drawable.sprite_enano_anciano)
                            2 to 1 -> characterImage.setImageResource(R.drawable.sprite_enano_adulto)
                            2 to 2 -> characterImage.setImageResource(R.drawable.sprite_enano_joven)

                            3 to 0 -> characterImage.setImageResource(R.drawable.sprite_maldito_anciano)
                            3 to 1 -> characterImage.setImageResource(R.drawable.sprite_maldito_adulto)
                            3 to 2 -> characterImage.setImageResource(R.drawable.sprite_maldito_joven)
                        }
                        pjRaza = Personaje.Raza.valueOf(parentRaza?.getItemAtPosition(positionRaza).toString())
                        pjEstadoVital = Personaje.EstadoVital.valueOf(parent?.getItemAtPosition(position).toString())
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

}