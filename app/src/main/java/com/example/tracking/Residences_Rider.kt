package com.example.tracking

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.tracking.Adapter.EventItemAdapter
import com.example.tracking.Adapter.MyAdapter_Rider
import com.example.tracking.Model.DeliveryDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase.*

class Residences_Rider : Fragment(){
    private lateinit var reference: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var userArrayList: ArrayList<DeliveryDetails>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_residences, container, false)
        userRecyclerview = view.findViewById(R.id.recycler_view)
        userRecyclerview.layoutManager = LinearLayoutManager(activity)

        return view

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userArrayList = arrayListOf()
        getDeliveryData()

    }

    private fun getDeliveryData() {
        val user = FirebaseAuth.getInstance().currentUser
        val userid = user!!.uid
        reference = FirebaseDatabase.getInstance().getReference("Profile")
        reference.child("z0E0W2DGo4QJNVeh0Cue2VmIV542").child("Delivery Details")
            .orderByChild("TimeStamps")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        for (detailsSnapshot in snapshot.children) {

                            val deliveryDetails =
                                detailsSnapshot.getValue(DeliveryDetails::class.java)
                            userArrayList.add(deliveryDetails!!)

                        }
                        userArrayList.sortWith(compareByDescending { it.TimeStamps })
                        userRecyclerview.adapter = MyAdapter_Rider(userArrayList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}