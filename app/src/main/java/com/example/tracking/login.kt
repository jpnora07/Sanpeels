@file:Suppress("DEPRECATION")

package com.example.tracking

import android.R.id.checkbox
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


@Suppress("DEPRECATION")
class login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    lateinit var reference: DatabaseReference
    lateinit var database: FirebaseDatabase
    lateinit var password: EditText

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth
        password = findViewById(R.id.passwordLogin)

        val showpass = findViewById<CheckBox>(R.id.showpass)
        val registerText: TextView = findViewById(R.id.textRegister)
        val loginButton: Button = findViewById(R.id.btnlogin)
        val resetButton: TextView = findViewById(R.id.btn_forgot_password)

        registerText.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            performLogin()
        }

        resetButton.setOnClickListener {

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Forgot Password")
            val view = layoutInflater.inflate(R.layout.dialog_forgotpassword, null)
            builder.setView(view)
            val username: EditText = view.findViewById(R.id.et_username)
            builder.setPositiveButton("Reset") { _, _ ->
                forgotPassword(username)
            }
            builder.setNegativeButton("Close") { _, _ -> }
            builder.show()
        }
        showpass.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, isChecked ->
            if (!isChecked) {
                // show password
                password.setTransformationMethod(PasswordTransformationMethod.getInstance())
            } else {
                // hide password
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
            }
        })
    }

    //if users forgot password
    private fun forgotPassword(username: EditText) {
        if (username.text.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(username.text.toString()).matches()) {
            return
        }
        Firebase.auth.sendPasswordResetEmail(username.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email Sent.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please Enter a Valid Email.", Toast.LENGTH_SHORT).show()
                }
            }

    }
    //login activity
    @SuppressLint("SuspiciousIndentation")
    private fun performLogin() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Logging in ...")
        progressDialog.setCancelable(false) // blocks UI interaction
        progressDialog.show()
        val email: EditText = findViewById(R.id.emailLogin)
        if (email.text.isEmpty() || password.text.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT)
                .show()
            progressDialog.hide()
            return
        }
        val emailInput = email.text.toString()
        val passwordInput = password.text.toString()

        auth.signInWithEmailAndPassword(emailInput, passwordInput)
            .addOnCompleteListener(this) { task ->
                if (emailInput == "collector@gmail.com" && passwordInput == "collector") {
                    val intent = Intent(this, Home_Rider::class.java)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this, "You login as collector", Toast.LENGTH_SHORT).show()
                    progressDialog.hide()
                } else {
                    if (task.isSuccessful) {
                        val intent = Intent(this, Home::class.java)
                        startActivity(intent)
                        Toast.makeText(
                            applicationContext, "Login Success",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressDialog.hide()
                    } else {
                        Toast.makeText(
                            applicationContext, "Authentication failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressDialog.hide()
                    } } }
            .addOnFailureListener(this) {
                progressDialog.hide() }
    }
}