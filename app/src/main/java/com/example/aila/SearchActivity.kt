package com.example.aila

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    private val voiceRecognitionRequestCode = 1001
    private lateinit var editTextSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        editTextSearch = findViewById(R.id.editTextSearch)

        // Botón para iniciar la búsqueda por voz
        val btnVoiceSearch = findViewById<Button>(R.id.btnVoiceSearch)
        btnVoiceSearch.setOnClickListener {
            startVoiceRecognition()
        }

        // Botón para realizar la búsqueda (cuando ya esté escrita)
        val btnSearch = findViewById<Button>(R.id.btnSearch)
        btnSearch.setOnClickListener {
            val searchQuery = editTextSearch.text.toString()
            if (searchQuery.isNotEmpty()) {
                openSearchInChrome(searchQuery)
            } else {
                Toast.makeText(this, "Por favor ingrese una búsqueda", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Iniciar el reconocimiento de voz
    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Di algo para buscar")
        startActivityForResult(intent, voiceRecognitionRequestCode)
    }

    // Recibir el resultado del reconocimiento de voz
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == voiceRecognitionRequestCode && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results?.get(0) ?: ""
            editTextSearch.setText(spokenText)
        }
    }

    // Abrir la búsqueda en Chrome
    private fun openSearchInChrome(query: String) {
        val searchUrl = "https://www.google.com/search?q=$query"
        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(searchUrl))
        startActivity(intent)
    }
}
