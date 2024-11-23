package com.example.aila

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtener la hora actual para el saludo dinámico
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            currentHour in 0..11 -> "Buenos días"
            currentHour in 12..17 -> "Buenas tardes"
            else -> "Buenas noches"
        }

        // Configurar mensaje de bienvenida
        val textViewGreeting = findViewById<TextView>(R.id.textViewGreeting)
        textViewGreeting.text = "$greeting, ¿en qué puedo ayudarte hoy?"

        // Configurar botones del menú
        findViewById<Button>(R.id.btnReminders).setOnClickListener {
            startActivity(Intent(this, ReminderActivity::class.java)) // Abrir Recordatorios
        }
        findViewById<Button>(R.id.btnSearch).setOnClickListener {
            // Implementar funcionalidad de búsqueda en el futuro
        }
        findViewById<Button>(R.id.btnNotes).setOnClickListener {
            // Implementar funcionalidad de notas en el futuro
        }
        findViewById<Button>(R.id.btnAlarms).setOnClickListener {
            // Implementar funcionalidad de alarmas en el futuro
        }
        findViewById<Button>(R.id.btnCalendar).setOnClickListener {
            // Implementar funcionalidad de calendario en el futuro
        }
    }
}
