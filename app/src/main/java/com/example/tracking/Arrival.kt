package com.example.tracking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tracking.Adapter.MyAdapter
import com.example.tracking.Adapter.MyArrivalAdapter
import com.example.tracking.Model.DeliveryDetails
import com.google.firebase.database.*

class Arrival : Fragment() {
    private lateinit var reference: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var userArrayList: ArrayList<DeliveryDetails>

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_arrival, container, false)
        userRecyclerview = view.findViewById(R.id.recycler_view)
        userRecyclerview.layoutManager = LinearLayoutManager(activity)
        return view
    }
    override fun onCreate(savedInstanceState: Bundle?) {super.onCreate(savedInstanceState)
        userArrayList = arrayListOf()
        getDeliveryData()
    }
    private fun getDeliveryData() {
        reference = FirebaseDatabase.getInstance().getReference("Profile")
        reference.child("z0E0W2DGo4QJNVeh0Cue2VmIV542")
            .child("Arrival_of_Collector").child("Distance").orderByChild("timestamp").limitToLast(10).addValueEventListener(
                object : ValueEventListener { override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                        for (detailsSnapshot in snapshot.children){
                            val deliveryDetails = detailsSnapshot.getValue(DeliveryDetails::class.java)
                            userArrayList.add(0,deliveryDetails!!)
                        }
                    }
                    userRecyclerview.adapter = MyArrivalAdapter(userArrayList)
                }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}