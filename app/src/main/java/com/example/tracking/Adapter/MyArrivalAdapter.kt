package com.example.tracking.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tracking.Model.DeliveryDetails
import com.example.tracking.R
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*


class MyArrivalAdapter (private val deliveryList: ArrayList<DeliveryDetails>) : RecyclerView.Adapter<MyArrivalAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.arrival_delivery_details_item ,parent ,false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder : MyViewHolder, position : Int) {

        val prettyTime = PrettyTime(Locale.getDefault())

        val currentitem = deliveryList[position].timestamp
        val ago: String = prettyTime.format(Date(currentitem!!))
        holder.timeArrive.text = ago
    }

    override fun getItemCount() : Int {
        return deliveryList.size

    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val timeArrive : TextView = itemView.findViewById(R.id.timeArrive)

    }

}
