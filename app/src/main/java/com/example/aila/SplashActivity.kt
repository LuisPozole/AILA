package com.example.aila

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.aila.MainActivity

import com.example.aila.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Aplica la animación combinada al logo
        val logo: ImageView = findViewById(R.id.logoImage)
        val combinedAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        logo.startAnimation(combinedAnimation)

        // Cambia a la actividad principal después de la animación
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finaliza el SplashActivity
        }, 2500) // Duración total aumentada para fluidez
    }
}