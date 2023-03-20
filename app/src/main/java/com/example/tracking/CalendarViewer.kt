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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.tracking.Adapter.EventItemAdapter
import com.example.tracking.Model.DeliveryDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarViewer : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener{
    private lateinit var swipeRefresh: SwipeRefreshLayout
    lateinit var calendarView: CalendarView
    lateinit var reference: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var userArrayList: ArrayList<DeliveryDetails>

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_viewer)
        val btnSetInfo = findViewById<Button>(R.id.btnSet)
        swipeRefresh = findViewById(R.id.swipe_refresh)
        swipeRefresh.setOnRefreshListener(this)
        swipeRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent))
        btnSetInfo.setOnClickListener { setinfo() }
        calendarView = findViewById(R.id.calendar)
        userRecyclerview = findViewById(R.id.recycler_view)
        userArrayList = arrayListOf()
        calendar()
        getDeliveryData()
        currentDate()
        val ExitBtn = findViewById<Button>(R.id.exitbtn)
        ExitBtn.setOnClickListener {
            val intent = Intent(this, Home::class.java)
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

    private fun getDeliveryData() {
        val user = FirebaseAuth.getInstance().currentUser
        val userid = user!!.uid
        reference = FirebaseDatabase.getInstance().getReference("Profile")
        reference.child(userid).child("Delivery Details")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (detailsSnapshot in snapshot.children) {
                            val deliveryDetails =
                                detailsSnapshot.getValue(DeliveryDetails::class.java)
                            userArrayList.add(deliveryDetails!!)
                        }
                        userArrayList.sortWith(compareByDescending { it.TimeStamps })
                        val adapter =
                            EventItemAdapter(userArrayList)
                        userRecyclerview.adapter = adapter
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

    private fun setinfo() {
        val intent = Intent(this, SetInfoCalendar::class.java)
        startActivity(intent)
    }

    override fun onRefresh() {
        // Indicate that the refresh is complete
        val intent = Intent(this, CalendarViewer::class.java)
        finish();
        overridePendingTransition( 0, 0);
        startActivity(intent);
        overridePendingTransition( 0, 0);
        swipeRefresh.isRefreshing = false
    }
}