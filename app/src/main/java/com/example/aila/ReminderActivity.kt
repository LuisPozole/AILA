package com.example.aila

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class ReminderActivity : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var textViewTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        // Inicializar vistas
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        textViewTime = findViewById(R.id.textViewTime)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        // Verificar permisos de alarmas exactas para Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!isExactAlarmPermissionGranted()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        // Configuración del campo de hora con TimePicker
        textViewTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this,
                { _, selectedHour, selectedMinute ->
                    textViewTime.text = String.format("%02d:%02d", selectedHour, selectedMinute)
                }, hour, minute, true)
            timePickerDialog.show()
        }

        // Configuración del botón para guardar recordatorio
        btnSubmit.setOnClickListener {
            val title = editTextTitle.text.toString()
            val description = editTextDescription.text.toString()
            val time = textViewTime.text.toString()

            if (title.isEmpty() || description.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Obtener la hora programada en milisegundos
            val calendar = Calendar.getInstance()
            val timeParts = time.split(":")
            calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
            calendar.set(Calendar.MINUTE, timeParts[1].toInt())
            calendar.set(Calendar.SECOND, 0)

            val alarmTimeInMillis = calendar.timeInMillis

            if (alarmTimeInMillis < System.currentTimeMillis()) {
                Toast.makeText(this, "La hora debe ser futura", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Configurar la alarma
            val intent = Intent(this, AlarmReceiver::class.java).apply {
                putExtra("EXTRA_TITLE", title)
                putExtra("EXTRA_DESCRIPTION", description)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                this,
                0, // Puedes usar un ID único para múltiples alarmas
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent)

            // Confirmar al usuario que el recordatorio fue programado
            Toast.makeText(this, "Recordatorio programado para las $time", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para verificar si el permiso para programar alarmas exactas ha sido concedido
    private fun isExactAlarmPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Verificar si el permiso para alarmas exactas está concedido
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.isIgnoringBatteryOptimizations(packageName)
        } else {
            true // No es necesario en versiones anteriores
        }
    }
}
