package com.example.tracking

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.tracking.Adapter.EventItem_RiderAdapter
import com.example.tracking.Adapter.MyAdapter_Rider
import com.example.tracking.Model.DeliveryDetails
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class CalendarViewer_Rider : AppCompatActivity() {
    lateinit var calendarView: CalendarView
    lateinit var reference: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var userArrayList: ArrayList<DeliveryDetails>

    private lateinit var myadapter: EventItem_RiderAdapter

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_viewer_rider)

        calendarView = findViewById(R.id.calendar)
        userRecyclerview = findViewById(R.id.recycler_view)
        userArrayList = arrayListOf()

        calendar()
        getDeliveryData()
        currentDate()

        val ExitBtn = findViewById<Button>(R.id.exitbtn)

        ExitBtn.setOnClickListener {
            val intent = Intent(this, Home_Rider::class.java)
            startActivity(intent)
        }
    }

    private fun currentDate() {
        val sdf = SimpleDateFormat("EEEE, MMM d, yyyy")
        val calendar: Calendar = Calendar.getInstance()
        val sDate = sdf.format(calendar.time)
        val dateTextView1 = findViewById<View>(R.id.date) as TextView
        dateTextView1.text = sDate
    }

    // fetch all users info ng kanilang mga inadd sa calendar at magshoshow sa calendar lang ng ridery
    private fun getDeliveryData() {
        reference = FirebaseDatabase.getInstance().getReference("Profile")

        reference.child("z0E0W2DGo4QJNVeh0Cue2VmIV542").child("Delivery Details")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        for (detailsSnapshot in snapshot.children) {

                            val deliveryDetails =
                                detailsSnapshot.getValue(DeliveryDetails::class.java)
                            userArrayList.add(deliveryDetails!!)

                        }
                        userArrayList.sortWith(compareByDescending { it.TimeStamps })
                        userRecyclerview.adapter = EventItem_RiderAdapter(userArrayList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SimpleDateFormat", "NewApi")
    private fun calendar() {
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val dateTextView1 = findViewById<View>(R.id.date) as TextView


            val sdf = SimpleDateFormat("EEEE, MMM d, yyyy")
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val sDate = sdf.format(calendar.time)
            dateTextView1.text = sDate
        }
    }


}