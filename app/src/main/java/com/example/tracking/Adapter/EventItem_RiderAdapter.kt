package com.example.tracking.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tracking.Model.DeliveryDetails
import com.example.tracking.R
import java.util.*

class EventItem_RiderAdapter (private val deliveryList: ArrayList<DeliveryDetails>) : RecyclerView.Adapter<EventItem_RiderAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.event_item_rider ,parent ,false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder : MyViewHolder, position : Int) {

        val currentitem = deliveryList[position]
        val details=currentitem.Details
        val upDetails = details?.capitalize()
        val name = currentitem.Name
        val words = name?.split(" ")?.toMutableList()
        var outputName = ""
        if (words != null) {
            for(word in words){
                outputName += word.capitalize() +" "
            }
        }
        outputName = outputName.trim()

        holder.txt_details.text = upDetails
        holder.txt_time.text =  currentitem.Date + " at " +currentitem.Time
        holder.txt_name.text = outputName
    }

    override fun getItemCount() : Int {
        return deliveryList.size

    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val txt_time : TextView = itemView.findViewById(R.id.txt_Time)
        val txt_name : TextView = itemView.findViewById(R.id.txt_Name)
        val txt_details : TextView = itemView.findViewById(R.id.txt_Details)

    }

}