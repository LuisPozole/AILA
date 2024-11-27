package com.example.aila

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.*

class ReminderActivity : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var textViewTime: TextView
    private lateinit var spinnerFrequency: Spinner
    private lateinit var btnSelectDays: Button
    private var selectedDays = mutableListOf<Int>() // Lista de días seleccionados

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        // Inicializar vistas
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        textViewTime = findViewById(R.id.textViewTime)
        spinnerFrequency = findViewById(R.id.spinnerFrequency)
        btnSelectDays = findViewById(R.id.btnSelectDays)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        checkNotificationPermission() // Verificar permisos de notificaciones

        // Manejar la selección de frecuencia en el Spinner
        spinnerFrequency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedOption = parent.getItemAtPosition(position).toString()
                if (selectedOption == "Seleccionar días específicos") {
                    btnSelectDays.visibility = View.VISIBLE
                } else {
                    btnSelectDays.visibility = View.GONE
                    selectedDays.clear() // Limpiar días seleccionados si no se usan
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Manejar la selección de días específicos al presionar el botón
        btnSelectDays.setOnClickListener {
            showDaySelectionDialog()
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
            val frequency = spinnerFrequency.selectedItem.toString()

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

            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

            if (frequency == "Diario") {
                // Configuración para repetición diaria
                val intent = createAlarmIntent(title, description)
                val pendingIntent = createPendingIntent(intent)
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    alarmTimeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            } else if (frequency == "Seleccionar días específicos") {
                if (selectedDays.isNotEmpty()) {
                    for (day in selectedDays) {
                        calendar.set(Calendar.DAY_OF_WEEK, day)
                        val intent = createAlarmIntent(title, description)
                        val pendingIntent = createPendingIntent(intent)
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    }
                    Toast.makeText(this, "Recordatorios programados para los días seleccionados", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No seleccionaste ningún día", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Mostrar diálogo para seleccionar días específicos
    private fun showDaySelectionDialog() {
        val daysOfWeek = arrayOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
        val checkedDays = BooleanArray(daysOfWeek.size) { day -> selectedDays.contains(day + 1) }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona los días")
        builder.setMultiChoiceItems(daysOfWeek, checkedDays) { _, which, isChecked ->
            if (isChecked) {
                selectedDays.add(which + 1)
            } else {
                selectedDays.remove(which + 1)
            }
        }
        builder.setPositiveButton("Aceptar") { _, _ ->
            Toast.makeText(this, "Días seleccionados: ${selectedDays.joinToString()}", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    // Verificar permisos de notificaciones
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission()
            } else {
                Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Permiso de notificaciones no requerido en esta versión", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Métodos auxiliares
    private fun createAlarmIntent(title: String, description: String): Intent {
        return Intent(this, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_TITLE", title)
            putExtra("EXTRA_DESCRIPTION", description)
        }
    }

    private fun createPendingIntent(intent: Intent): PendingIntent {
        return PendingIntent.getBroadcast(
            this,
            intent.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
