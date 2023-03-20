package com.example.tracking

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class SetInfoCalendar : AppCompatActivity() {
    lateinit var reference: DatabaseReference
    lateinit var database: FirebaseDatabase
    lateinit var txtdate: TextView
    lateinit var txttime: TextView
    lateinit var txtdateT: TextView

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_info_calendar)

        val btnDatePicker = findViewById<ImageButton>(R.id.imgBtn_datePicker)
        val btnTimePicker = findViewById<ImageButton>(R.id.imgBtn_TimePicker)
        val btnSetDT: Button = findViewById(R.id.setDateTime)

        txtdate = findViewById(R.id.textDate)
        txtdateT = findViewById(R.id.textDate2)
        txttime = findViewById(R.id.textTime)
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        btnDatePicker.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, month, dayOfMonth ->
                    val sdf = SimpleDateFormat("EEEE, MMM d, yyyy")
                    val sdft = SimpleDateFormat("dd-MM-yyyy")
                    val calendar: Calendar = Calendar.getInstance()
                    calendar.set(year, month, dayOfMonth)
                    val sDate = sdf.format(calendar.time)
                    val sDatet = sdft.format(calendar.time)
                    txtdateT.text = sDatet
                    txtdate.text = sDate
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }



        btnTimePicker.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)

                txttime.text = SimpleDateFormat("hh:mm aa").format(cal.time)
            }
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
            ).show()
        }

        btnSetDT.setOnClickListener {
            setDateAndTime()
        }

        fetchUserName()

        val ExitBtn = findViewById<Button>(R.id.exitbtn)

        ExitBtn.setOnClickListener {
            val intent = Intent(this, CalendarViewer::class.java)
            startActivity(intent)
        }

    }

    private fun fetchUserName() {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("Profile")
        val txtFullname = findViewById<View>(R.id.fname) as TextView
        val user = FirebaseAuth.getInstance().currentUser
        val userid = user!!.uid
        reference.child(userid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val fname = dataSnapshot.child("Firstname").value as String?
                val lname = dataSnapshot.child("Lastname").value as String?
                txtFullname.text = fname + " " + lname

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "Something wrong happened!", Toast.LENGTH_LONG)
                    .show()
            }

        })


    }

    private fun setDateAndTime() {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("Profile")
        val user = FirebaseAuth.getInstance().currentUser
        val userid = user!!.uid
        reference.child(userid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val txtdate = findViewById<TextView>(R.id.textDate)
                val txtdateT = findViewById<TextView>(R.id.textDate2)
                val txttime = findViewById<TextView>(R.id.textTime)
                val txtDetails = findViewById<TextView>(R.id.textDetails)
                val txtFullname = findViewById<TextView>(R.id.fname)
                val inputDate = txtdate.text.toString()
                val inputDateT = txtdateT.text.toString()
                val inputTime = txttime.text.toString()
                val inputDetails = txtDetails.text.toString()
                val inputName = txtFullname.text.toString()
                val key = reference.push().key
                val dateAndTime = reference.child(userid).child("Delivery Details").child(key!!)
                val dateAndTimeCollector =
                    reference.child("z0E0W2DGo4QJNVeh0Cue2VmIV542").child("Delivery Details").child(key!!)

                if (txtdate.text.isEmpty() || txttime.text.isEmpty() || txtDetails.text.isEmpty()) {
                    Toast.makeText(applicationContext, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                    return
                }

                val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy")
                val dateT = formatter.parse(inputDateT) as Date

                dateAndTime.child("Details").setValue(inputDetails)
                dateAndTime.child("Date").setValue(inputDate)
                dateAndTime.child("Time").setValue(inputTime)
                dateAndTime.child("Name").setValue(inputName)
                dateAndTime.child("TimeStamps").setValue(dateT.time)
                dateAndTime.child("UsersId").setValue(userid)
                dateAndTime.child("key").setValue(key)
                //for collector
                dateAndTimeCollector.child("Details").setValue(inputDetails)
                dateAndTimeCollector.child("Date").setValue(inputDate)
                dateAndTimeCollector.child("Time").setValue(inputTime)
                dateAndTimeCollector.child("TimeStamps").setValue(dateT.time)
                dateAndTimeCollector.child("Name").setValue(inputName)
                dateAndTimeCollector.child("UsersId").setValue(userid)
                dateAndTimeCollector.child("key").setValue(key)
                    //dateAndTimeCollector.child("timestamp").setValue(ServerValue.TIMESTAMP)
                    //updateAndTime.child("timestamp").setValue(ServerValue.TIMESTAMP)
                    //dateAndTime.child("timestamp").setValue(ServerValue.TIMESTAMP)
                    .addOnSuccessListener {
                        val intent = Intent(applicationContext, CalendarViewer::class.java)
                        startActivity(intent)
                        Toast.makeText(
                            applicationContext,
                            " You set your delivery date and time",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT)
                            .show()
                    }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "Something wrong happened!", Toast.LENGTH_LONG)
                    .show()
            }

        })

    }

}