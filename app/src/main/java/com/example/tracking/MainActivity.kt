package com.example.tracking

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tracking.R
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.tracking.login
import com.example.tracking.opening
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth;
    var databaseReference: DatabaseReference? = null
    var database: FirebaseDatabase? = null
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Profile")

        val registerButton: Button = findViewById(R.id.btnreg)
        val cancelButton: Button = findViewById(R.id.btncancel)
        val loginButton: TextView = findViewById(R.id.textLogin)

        registerButton.setOnClickListener {
            performSignUp()
        }

        cancelButton.setOnClickListener {
            val intent = Intent(this, opening::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val intent = Intent(this, login::class.java)
            startActivity(intent)
        }
    }

    private fun performSignUp() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading ...")
        progressDialog.setCancelable(false) // blocks UI interaction
        progressDialog.show()
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val fname = findViewById<EditText>(R.id.txtfieldfirstname)
        val lname = findViewById<EditText>(R.id.txtfieldlastname)
        val cn = findViewById<EditText>(R.id.txtfieldcontact)
        val adr = findViewById<EditText>(R.id.txtfieldaddress)
        val inputEmail = email.text.toString()
        val inputPassword = password.text.toString()
        val inputFirstname = fname.text.toString()
        val inputLastname = lname.text.toString()
        val inputContacts = cn.text.toString()
        val inputAddress = adr.text.toString()

        if (email.text.isEmpty() || password.text.isEmpty() || fname.text.isEmpty() || lname.text.isEmpty() || cn.text.isEmpty() || adr.text.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT)
                .show()
            progressDialog.hide()
            return
        }
        auth.createUserWithEmailAndPassword(inputEmail, inputPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, login::class.java)
                    startActivity(intent)
                    val currentUser = auth.currentUser
                    val currentUSerDb = databaseReference?.child((currentUser?.uid!!))

                    currentUSerDb?.child("Email")?.setValue(inputEmail)
                    currentUSerDb?.child("Password")?.setValue(inputPassword)
                    currentUSerDb?.child("Firstname")?.setValue(inputFirstname)
                    currentUSerDb?.child("Lastname")?.setValue(inputLastname)
                    currentUSerDb?.child("Contacts")?.setValue(inputContacts)
                    currentUSerDb?.child("Address")?.setValue(inputAddress)
                    currentUSerDb?.child("id")?.setValue("1")
                    Toast.makeText(baseContext, "Registration Successfully", Toast.LENGTH_SHORT)
                        .show()
                    progressDialog.hide()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                    progressDialog.hide()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error occurred ${it.localizedMessage}", Toast.LENGTH_SHORT)
                    .show()
                progressDialog.hide()
            }

    }


}