package com.example.proyecto_ppg

import Personaje
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.dialogflow.v2.SessionName
import com.google.cloud.dialogflow.v2.SessionsClient
import com.google.cloud.dialogflow.v2.SessionsSettings
import java.util.ArrayList
import java.util.UUID


class ChatBotActivity : AppCompatActivity() {
    private var messageList: ArrayList<Message> = ArrayList()

    //dialogFlow
    private var sessionsClient: SessionsClient? = null
    private var sessionName: SessionName? = null
    private val uuid = UUID.randomUUID().toString()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var btnSend: ImageButton
    private lateinit var editMessage: EditText
    private lateinit var chatView: RecyclerView
    private lateinit var backButton: Button
    private lateinit var barra : UtilBar
    private lateinit var objPersonaje: Personaje

    private var message = ""
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_bot)

        initialize()

        addMessageToList(message, false)
        sendMessageToBot(message)

        //onclick listener to update the list and call dialogflow
        btnSend.setOnClickListener {
            message= editMessage.text.toString()
            if (message.isNotEmpty()) {
                addMessageToList(message, false)
                sendMessageToBot(message)
            } else {
                Toast.makeText(this, "Please enter text!", Toast.LENGTH_SHORT).show()
            }
        }

        //initialize bot config
        setUpBot()

        backButton.setOnClickListener {
            val intent = Intent(this,CityEnterActivity::class.java)
            intent.putExtra("personaje",objPersonaje)
            startActivity(intent)
        }
    }

    @Suppress("DEPRECATION")
    private fun initialize(){
        backButton = findViewById(R.id.backChatButton)
        btnSend = findViewById(R.id.btnSend)
        editMessage = findViewById(R.id.editMessage)
        chatView = findViewById(R.id.chatView)
        //setting adapter to recyclerview
        chatAdapter = ChatAdapter(this, messageList)
        chatView.adapter = chatAdapter

        objPersonaje = intent.getParcelableExtra("personaje")!!
        message = "Mi estado vital es ${objPersonaje.getEstadoVital()}"

        barra = UtilBar(this,objPersonaje)
        barra.setupToolbar(this,R.id.barraOpciones)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addMessageToList(message: String, isReceived: Boolean) {
        messageList.add(Message(message, isReceived))
        editMessage.setText("")
        chatAdapter.notifyDataSetChanged()
        chatView.layoutManager?.scrollToPosition(messageList.size - 1)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        barra.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return barra.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    private fun setUpBot() {
        try {
            val stream = this.resources.openRawResource(R.raw.credential)
            val credentials: GoogleCredentials = GoogleCredentials.fromStream(stream)
                .createScoped("https://www.googleapis.com/auth/cloud-platform")
            val projectId: String = (credentials as ServiceAccountCredentials).projectId
            val settingsBuilder: SessionsSettings.Builder = SessionsSettings.newBuilder()
            val sessionsSettings: SessionsSettings = settingsBuilder.setCredentialsProvider(
                FixedCredentialsProvider.create(credentials)
            ).build()
            sessionsClient = SessionsClient.create(sessionsSettings)
            sessionName = SessionName.of(projectId, uuid)
            Log.d("chatbot", "projectId : $projectId")
        } catch (e: Exception) {
            Log.d("chatbot", "setUpBot: " + e.message)
        }
    }

    private fun sendMessageToBot(message: String) {
        val respuesta = objPersonaje.comunicacion(message)
        addMessageToList(respuesta,true)
    }
}