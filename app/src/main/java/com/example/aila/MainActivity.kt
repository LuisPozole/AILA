package com.example.aila

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.enableEdgeToEdge
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var textViewTime: TextView
    private val VOICE_RECOGNITION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Ajusta los insets para la compatibilidad con el edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar vistas
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        textViewTime = findViewById(R.id.textViewTime)
        val btnVoiceInput = findViewById<Button>(R.id.btnVoiceInput)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        // Configuración del botón para reconocimiento de voz
        btnVoiceInput.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE)
        }

        // Configuración del campo de hora con TimePicker
        textViewTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this,
                { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                    textViewTime.text = String.format("%02d:%02d", selectedHour, selectedMinute)
                }, hour, minute, true)
            timePickerDialog.show()
        }

        // Configuración del botón para enviar datos a DisplayActivity
        btnSubmit.setOnClickListener {
            val title = editTextTitle.text.toString()
            val description = editTextDescription.text.toString()
            val time = textViewTime.text.toString()

            // Crea el Intent y agrega los datos
            val intent = Intent(this, DisplayActivity::class.java).apply {
                putExtra("EXTRA_TITLE", title)
                putExtra("EXTRA_DESCRIPTION", description)
                putExtra("EXTRA_TIME", time)
            }

            // Inicia la segunda actividad
            startActivity(intent)
        }
    }

    // Recibe el texto de reconocimiento de voz
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            editTextDescription.setText(result?.get(0) ?: "")
        }
    }
}
