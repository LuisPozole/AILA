package com.example.aila

import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val btnAddEvent = findViewById<FloatingActionButton>(R.id.btnAddEvent)

        // Detectar selección de fecha
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            Toast.makeText(this, "Fecha seleccionada: $selectedDate", Toast.LENGTH_SHORT).show()
        }

        // Manejar botón para agregar eventos
        btnAddEvent.setOnClickListener {
            // Aquí puedes abrir un diálogo para agregar un evento o iniciar una nueva actividad
            Toast.makeText(this, "Agregar evento (pendiente)", Toast.LENGTH_SHORT).show()
        }
    }
}
