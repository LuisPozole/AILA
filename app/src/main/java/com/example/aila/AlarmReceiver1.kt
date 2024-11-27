package com.example.aila

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmReceiver1 : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "¡Alarma sonando!", Toast.LENGTH_LONG).show()
        // Aquí puedes agregar una notificación o abrir otra actividad si lo deseas
    }
}
