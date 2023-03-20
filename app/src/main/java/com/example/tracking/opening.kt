package com.example.tracking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tracking.R
import android.content.Intent
import android.widget.Button
import com.example.tracking.MainActivity
import com.example.tracking.login

//The second page or activity of app

class opening : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opening)
        val btnreg = findViewById<Button>(R.id.btnreg)
        val btnlogin = findViewById<Button>(R.id.btnlogin)
        btnreg.setOnClickListener { register() }
        btnlogin.setOnClickListener { login() }
    }

    fun register() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun login() {
        val intent = Intent(this, login::class.java)
        startActivity(intent)
    }
}