package com.example.tracking.Adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.tracking.Model.DeliveryDetails
import com.example.tracking.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

@Suppress("DEPRECATION")
class MyAdapter_Rider(private val deliveryList: ArrayList<DeliveryDetails>) :
    RecyclerView.Adapter<MyAdapter_Rider.MyViewHolder>() {


    private lateinit var progressDialog: ProgressDialog
    lateinit var reference: DatabaseReference
    lateinit var database: FirebaseDatabase
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.all_delivery_details_item, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("ResourceAsColor", "SuspiciousIndentation")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        database = FirebaseDatabase.getInstance()
        reference = database.getReference("Profile")
        val index = 0
        val currentitem = deliveryList[position]
        val name = currentitem.Name
        val dateM = currentitem.Date
        val words = name?.split(" ")?.toMutableList()
        var outputName = ""
        if (words != null) {
            for (word in words) {
                outputName += word.capitalize() + " "
            }
        }
        outputName = outputName.trim()
        holder.txt_date.text = dateM
        holder.txt_time.text = currentitem.Time
        holder.txt_name.text = outputName
        holder.done.setOnClickListener {
            notifyItemRangeChanged(index, itemCount)
            holder.done.isEnabled = false
            holder.idPBLoading.visibility = View.VISIBLE
                    if (index in 0 until deliveryList.size) {
                        holder.done.isEnabled = false
                        reference.child("z0E0W2DGo4QJNVeh0Cue2VmIV542").child("Delivery Details")
                            .child(currentitem.key!!).removeValue()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    holder.done.isEnabled = true
                                    deliveryList.removeAt(index)
                                    notifyDataSetChanged()
                                    val context = holder.itemView.context
                                    /*Toast.makeText(
                                        context,
                                        "Successfully mark as done the collection on $dateM",
                                        Toast.LENGTH_SHORT
                                    ).show()*/
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


    }

    override fun getItemCount(): Int {
        return deliveryList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val txt_name: TextView = itemView.findViewById(R.id.fullname)
        val txt_date: TextView = itemView.findViewById(R.id.dateDetails)
        val txt_time: TextView = itemView.findViewById(R.id.timeDetails)
        val done: TextView = itemView.findViewById(R.id.MarkAsDone)
        val idPBLoading: ProgressBar = itemView.findViewById(R.id.idPBLoading)
    }

}