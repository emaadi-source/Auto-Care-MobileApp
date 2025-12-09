package com.haris.semesterproject.provider.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.R
import com.haris.semesterproject.provider.data.ItemType
import com.haris.semesterproject.provider.data.ServiceItem

class ServicePartAdapter(
    private var items: List<ServiceItem>,
    // Callback function: Passing the ServiceItem back when delete is clicked
    private val onDeleteClick: (ServiceItem) -> Unit
) : RecyclerView.Adapter<ServicePartAdapter.ViewHolder>() {

    fun updateList(newItems: List<ServiceItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvName)
        val brand: TextView = view.findViewById(R.id.tvBrand)
        val price: TextView = view.findViewById(R.id.tvPrice)
        val meta: TextView = view.findViewById(R.id.tvMeta) // Stock or Duration
        val chipCategory: TextView = view.findViewById(R.id.chipCategory)

        // Ensure your XML has this ID for the trash icon
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.name.text = item.name
        holder.chipCategory.text = item.category
        holder.price.text = "₹${item.price.toInt()}"

        // --- LOGIC TO SHOW/HIDE FIELDS BASED ON TYPE ---
        if (item.type == ItemType.PART) {
            // It's a Spare Part
            holder.brand.visibility = View.VISIBLE
            holder.brand.text = "Brand: ${item.brand ?: "N/A"}"

            holder.meta.text = "Stock: ${item.stock ?: 0}"
            holder.meta.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.status_green_text))
        } else {
            // It's a Service
            holder.brand.visibility = View.GONE

            holder.meta.text = "Duration: ${item.duration ?: "N/A"}"
            holder.meta.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.text_gray))
        }

        // --- CLICK LISTENER ---
        holder.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount() = items.size
}