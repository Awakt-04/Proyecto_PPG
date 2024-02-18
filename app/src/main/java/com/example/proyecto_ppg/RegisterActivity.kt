package com.example.proyecto_ppg

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.util.Locale

class RegisterActivity : AppCompatActivity(),OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var registerText : TextView
    private lateinit var registerEmail: EditText
    private lateinit var registerPassword: EditText
    private lateinit var registerRepeatPassword: EditText
    private lateinit var registerButton: Button
    private lateinit var registerGoLoginButton: Button
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initialize()

        registerButton.setOnClickListener{
            confirmReg()
        }

        registerGoLoginButton.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }


    }

    private fun initialize(){
        auth = Firebase.auth
        registerButton = findViewById(R.id.registerButton)
        registerEmail = findViewById(R.id.registerEmail)
        registerGoLoginButton = findViewById(R.id.registerGoLoginButton)
        registerPassword = findViewById(R.id.registerPassword)
        registerRepeatPassword = findViewById(R.id.registerRepeatPassword)
        registerText = findViewById(R.id.registerText)
        tts = TextToSpeech(this, this)
    }

    private fun confirmReg() {
        val email = registerEmail.text.toString()
        val password = registerPassword.text.toString()
        val repeatPassword = registerRepeatPassword.text.toString()

        if (validateInput(email, password, repeatPassword)) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }else
                        Toast.makeText(this, "Error register", Toast.LENGTH_SHORT).show()
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
                    delayAndSpeak(registerText.text.toString(),2000)
                    delayAndSpeak(registerEmail.hint.toString(),4000)
                    delayAndSpeak(registerPassword.hint.toString(),6000)
                    delayAndSpeak(registerRepeatPassword.hint.toString(),8000)
                    delayAndSpeak(registerButton.text.toString(),10000)
                    delayAndSpeak(registerGoLoginButton.text.toString(),12000)
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

    private fun validateInput(e: String, p: String, rp: String): Boolean{
        when{
            p.isBlank() -> {
                registerPassword.error = getString(R.string.PasswordBlankError)
                registerPassword.requestFocus()
                return false
            }
            p.length < 8 -> {
                registerPassword.error = getString(R.string.PasswordLengthError)
                registerPassword.requestFocus()
                return false
            }

            !p.contains(Regex("[A-Z]")) -> {
                registerPassword.error = getString(R.string.PasswordCapitalError)
                registerPassword.requestFocus()
                return false

            }

            !p.contains(Regex("[0-9]")) -> {
                registerPassword.error = getString(R.string.PasswordNumberError)
                registerPassword.requestFocus()
                return false
            }

            !p.contains(Regex("[\\W_]")) -> {
                registerPassword.error = getString(R.string.PasswordSimbolError)
                registerPassword.requestFocus()
                return false
            }
            p != rp -> {
                Toast.makeText(this,R.string.PasswordDifferent,Toast.LENGTH_SHORT).show()
                registerPassword.requestFocus()
                registerRepeatPassword.requestFocus()
                return false
            }
            e.isBlank() -> {
                registerEmail.error = getString(R.string.EmailBlankError)
                registerEmail.requestFocus()
                return false
            }
            !e.contains("@") -> {
                registerEmail.error = getString(R.string.EmailAtError)
                registerEmail.requestFocus()
                return false
            }
            !e.matches(Regex(".+\\.(com|net|org|gov|edu|mil|int|arpa|eu|es)$")) -> {
                registerEmail.error = getString(R.string.EmailDomainError)
                registerEmail.requestFocus()
                return false
            }
            else -> return true
        }
    }
}