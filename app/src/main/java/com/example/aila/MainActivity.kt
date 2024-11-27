package com.example.aila

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    // Solicitud de permisos para notificaciones
    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "Permiso de notificaciones requerido", Toast.LENGTH_SHORT).show()
        }
    }

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

        // Solicitar permisos de notificación si es Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Configurar botones del menú
        findViewById<Button>(R.id.btnReminders).setOnClickListener {
            startActivity(Intent(this, ReminderActivity::class.java)) }

        findViewById<Button>(R.id.btnSearch).setOnClickListener {
            // Iniciar la nueva actividad de búsqueda
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnNotes).setOnClickListener {
            startActivity(Intent(this, NotesActivity::class.java))
        }

        findViewById<Button>(R.id.btnAlarms).setOnClickListener {
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnCalendar).setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

    }

    // Verificar permiso para alarmas exactas
    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!isExactAlarmPermissionGranted()) {
                AlertDialog.Builder(this)
                    .setTitle("Permiso necesario")
                    .setMessage("Se requiere permiso para programar alarmas exactas. ¿Deseas permitirlo?")
                    .setPositiveButton("Permitir") { _, _ ->
                        requestPermissions(
                            arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM),
                            100
                        )
                    }
                    .setNegativeButton("Cancelar") { _, _ ->
                        Toast.makeText(this, "No se podrá programar alarmas exactas", Toast.LENGTH_SHORT).show()
                    }
                    .show()
            }
        }
    }

    // Verificar si el permiso para alarmas exactas está otorgado
    private fun isExactAlarmPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SCHEDULE_EXACT_ALARM
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
