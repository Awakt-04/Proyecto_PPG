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
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.util.Locale


class LoginActivity : AppCompatActivity(),OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var loginText: TextView
    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var loginGoRegisterButton: Button

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initialize()

        loginButton.setOnClickListener {
            confirmLog()
        }

        loginGoRegisterButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun initialize(){
        auth = Firebase.auth
        loginButton = findViewById(R.id.loginButton)
        loginEmail = findViewById(R.id.loginEmail)
        loginGoRegisterButton = findViewById(R.id.loginGoRegisterButton)
        loginPassword = findViewById(R.id.loginPassword)
        loginText = findViewById(R.id.logInText)
        tts = TextToSpeech(this, this)
    }

    private fun backUpPj() {
        val db = DatabaseHelper(this)
        db.crearTablasSiNoExisten()
        val objPersonaje: Personaje? = db.getPersona(auth.uid!!)
        if (objPersonaje != null) {
            objPersonaje.setUid(auth.uid!!)
            val intent = Intent(this, DiceActivity::class.java)
            intent.putExtra("personaje", objPersonaje)
            startActivity(intent)
        } else {
            val intent = Intent(this, CreationActivity::class.java)
            intent.putExtra("uid", auth.uid)
            startActivity(intent)
        }

    }

    private fun confirmLog(){
        val email: String = loginEmail.text.toString()
        val password: String = loginPassword.text.toString()
        if (validateInput(email, password)) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        backUpPj()
                        finish()
                    }
                }
        }
    }

    private fun delayAndSpeak(text: String, tiempo: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            speak(text)
        },tiempo)
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
                    delayAndSpeak(loginText.text.toString(),2000)
                    delayAndSpeak(loginEmail.hint.toString(),4000)
                    delayAndSpeak(loginPassword.hint.toString(),6000)
                    delayAndSpeak(loginButton.text.toString(),8000)
                    delayAndSpeak(loginGoRegisterButton.text.toString(),10000)
                },10000)

            }
        } else {
            Log.e("TTS Error","Error al cargar")
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

    private fun validateInput(e: String, p: String): Boolean{
        return when {
            e.isBlank() -> {
                loginEmail.error = getString(R.string.EmailBlankError)
                loginEmail.requestFocus()
                false
            }

            p.isBlank() -> {
                loginPassword.error = getString(R.string.PasswordBlankError)
                loginPassword.requestFocus()
                false
            }

            else -> true
        }
    }

}