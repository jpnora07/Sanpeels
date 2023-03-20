package com.example.tracking.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tracking.Model.DeliveryDetails
import com.example.tracking.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyAdapter(private val deliveryList: ArrayList<DeliveryDetails>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.all_delivery_details_item, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = deliveryList[position]
        val name = currentitem.Name
        val words = name?.split(" ")?.toMutableList()
        var outputName = ""
        if (words != null) {
            for (word in words) {
                outputName += word.capitalize() + " "
            }
        }
        outputName = outputName.trim()
        holder.txt_date.text = currentitem.Date
        holder.txt_time.text = currentitem.Time
        holder.txt_name.text = outputName
        holder.btn_collected.visibility = View.GONE



        //if (currentitem.Date?.equals(sDate) == true) {
        //holder.txt_date.setTextColor(R.color.colorPrimary)
        //holder.txt_date.setText(sDate)


        // }else{
        //   holder.itemView.visibility = View.GONE
        //}
    }

    override fun getItemCount(): Int {
        return deliveryList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val txt_name: TextView = itemView.findViewById(R.id.fullname)
        val txt_date: TextView = itemView.findViewById(R.id.dateDetails)
        val txt_time: TextView = itemView.findViewById(R.id.timeDetails)
        val btn_collected: TextView = itemView.findViewById(R.id.MarkAsDone)
    }

}