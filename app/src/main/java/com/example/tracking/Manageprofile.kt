package com.example.tracking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Manageprofile : AppCompatActivity() {
    lateinit var reference: DatabaseReference
    lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manageprofile)

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("Profile")

        val user = FirebaseAuth.getInstance().currentUser
        val userid = user!!.uid
        val fullnameTextView = findViewById<View>(R.id.fname) as TextView
        val emailTextView = findViewById<View>(R.id.ProfileEmail) as TextView
        val numberTextView = findViewById<View>(R.id.number) as TextView
        val addressTextView = findViewById<View>(R.id.address) as TextView
        reference.child(userid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val fname = dataSnapshot.child("Firstname").value as String?
                val email = dataSnapshot.child("Email").value as String?
                val lname = dataSnapshot.child("Lastname").value as String?
                val cnumber = dataSnapshot.child("Contacts").value as String?
                val address = dataSnapshot.child("Address").value as String?
                val fullname = fname + " " + lname
                val wordsAdd = address?.split(" ")?.toMutableList()
                var outputAdd = ""
                if (wordsAdd != null) {
                    for (word in wordsAdd) {outputAdd += word.capitalize() + " "}
                }
                outputAdd = outputAdd.trim()
                val words = fullname?.split(" ")?.toMutableList()
                var outputName = ""
                if (words != null) {
                    for (word in words) {outputName += word.capitalize() + " "}
                }
                outputName = outputName.trim()
                fullnameTextView.text = outputName
                emailTextView.text = email
                numberTextView.text = cnumber
                addressTextView.text = outputAdd
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "Something wrong happened!", Toast.LENGTH_LONG)
                    .show()} })

        /*val ExitBtn: Button = findViewById(R.id.exitbtn)

        ExitBtn.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)
            startActivity(intent)
        }*/
    }
}