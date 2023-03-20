package com.example.tracking.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.tracking.Model.DeliveryDetails
import com.example.tracking.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class EventItemAdapter(private val deliveryList: ArrayList<DeliveryDetails>) :
    RecyclerView.Adapter<EventItemAdapter.MyViewHolder>() {

    lateinit var reference: DatabaseReference
    lateinit var database: FirebaseDatabase
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("Profile")
        val index = 0
        val user = FirebaseAuth.getInstance().currentUser
        val userid = user!!.uid
        val currentitem = deliveryList[position]
        val dateM = currentitem.Date
        holder.txt_details.text = currentitem.Details?.capitalize()
        holder.txt_time.text = currentitem.Time
        holder.txt_date.text = dateM

        holder.done.setOnClickListener {
            notifyItemRangeChanged(index, itemCount)
            holder.done.isEnabled = false
            holder.idPBLoading.visibility = View.VISIBLE
            AlertDialog.Builder(holder.itemView.context)
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete") { _, _ ->
                    // Refresh the activity

                    if (index in 0 until deliveryList.size) {
                        holder.done.isEnabled = false
                        reference.child(userid).child("Delivery Details")
                            .child(currentitem.key!!).removeValue()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    holder.done.isEnabled = true
                                    deliveryList.removeAt(index)
                                    notifyDataSetChanged()
                                    val context = holder.itemView.context
                                    Toast.makeText(
                                        context,
                                        "Successfully deleted the $dateM",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    notifyItemRangeChanged(index, itemCount)
                                    //(holder.itemView.context as Activity).recreate()
                                    // Remove activity transition animation
                                    //(holder.itemView.context as Activity).overridePendingTransition(0, 0)
                                    holder.idPBLoading.visibility = View.GONE

                                } else {
                                    holder.done.isEnabled = true
                                    holder.idPBLoading.visibility = View.GONE
                                    // Handle deletion failure
                                    Log.e("MyAdapter", "Index out of bounds: $index")
                                }
                            }
                    } else {
                        Log.e("MyAdapter", "Index out of bounds: $index")
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun getItemCount(): Int {
        return deliveryList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txt_time: TextView = itemView.findViewById(R.id.txt_Time)
        val txt_details: TextView = itemView.findViewById(R.id.txt_Details)
        val txt_date: TextView = itemView.findViewById(R.id.txt_Date)
        val done: ImageView = itemView.findViewById(R.id.done)
        val idPBLoading: ProgressBar = itemView.findViewById(R.id.idPBLoading)

    }

}