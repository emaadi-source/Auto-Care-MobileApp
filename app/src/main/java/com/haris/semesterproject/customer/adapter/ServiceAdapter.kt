package com.haris.semesterproject.customer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.R
import com.haris.semesterproject.customer.data.ServiceWhole

class ServiceAdapter(
    private val services: List<ServiceWhole>,
    private val selectedServices: HashMap<Int, Double>,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvServiceName) // Ensure IDs match your item_service.xml
        val price: TextView = view.findViewById(R.id.tvServicePrice)
        val checkBox: CheckBox = view.findViewById(R.id.cbSelectService)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]

        holder.name.text = service.name
        holder.price.text = "₹${service.price}"

        // Convert service.id to Int safely
        val serviceId = try { service.id.toInt() } catch (e: Exception) { 0 }

        // Remove listener temporarily to avoid trigger during scrolling
        holder.checkBox.setOnCheckedChangeListener(null)

        holder.checkBox.isChecked = selectedServices.containsKey(serviceId)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedServices[serviceId] = service.price
            } else {
                selectedServices.remove(serviceId)
            }
            onSelectionChanged()
        }
    }

    override fun getItemCount() = services.size
}