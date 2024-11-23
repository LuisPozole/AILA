package com.example.aila

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_display)
        // Obtén los datos del Intent
        val title = intent.getStringExtra("EXTRA_TITLE")
        val description = intent.getStringExtra("EXTRA_DESCRIPTION")
        val time = intent.getStringExtra("EXTRA_TIME")

        // Muestra los datos en los TextViews
        findViewById<TextView>(R.id.textViewTitle).text = "Título: $title"
        findViewById<TextView>(R.id.textViewDescription).text = "Descripción: $description"
        findViewById<TextView>(R.id.textViewTime).text = "Hora: $time"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}