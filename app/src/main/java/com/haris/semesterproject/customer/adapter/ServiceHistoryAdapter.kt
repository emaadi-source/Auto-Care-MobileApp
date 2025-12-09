package com.haris.semesterproject.customer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.customer.data.ServiceHistory
import com.haris.semesterproject.R

class ServiceHistoryAdapter(private val items: List<ServiceHistory>) :
    RecyclerView.Adapter<ServiceHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workshopName: TextView = itemView.findViewById(R.id.tvWorkshopName)
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val services: TextView = itemView.findViewById(R.id.tvServices)
        val price: TextView = itemView.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = items[position]
        holder.workshopName.text = history.workshopName
        holder.date.text = "${history.date} • ${history.time}"
        holder.services.text = history.services.joinToString(", ")
        holder.price.text = history.price
    }

    override fun getItemCount(): Int = items.size
}
