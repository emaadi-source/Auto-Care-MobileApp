package com.haris.semesterproject.provider.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.R
import com.haris.semesterproject.provider.data.ItemType
import com.haris.semesterproject.provider.data.ServiceItem

class ServicePartAdapter(
    private var items: MutableList<ServiceItem>,
    private val onDeleteClick: (ServiceItem) -> Unit,
    private val onToggleClick: (ServiceItem, Boolean) -> Unit
) : RecyclerView.Adapter<ServicePartAdapter.ViewHolder>() {

    private var originalList = mutableListOf<ServiceItem>()

    // Use this instead of updateList to initialize backup list for search
    fun setData(newItems: List<ServiceItem>) {
        items = newItems.toMutableList()
        originalList = newItems.toMutableList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        items = if (query.isEmpty()) {
            originalList.toMutableList()
        } else {
            originalList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvName)
        val brand: TextView = view.findViewById(R.id.tvBrand)
        val price: TextView = view.findViewById(R.id.tvPrice)
        val meta: TextView = view.findViewById(R.id.tvMeta)
        val chipCategory: TextView = view.findViewById(R.id.chipCategory)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
        // Ensure XML has Switch with ID 'switchActive'
        val switchActive: Switch = view.findViewById(R.id.switchActive)
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
        holder.price.text = "Rs ${item.price.toInt()}"

        // Handle Switch without triggering listener during scroll
        holder.switchActive.setOnCheckedChangeListener(null)
        holder.switchActive.isChecked = (item.is_active == 1)

        if (item.type == ItemType.PART) {
            holder.brand.visibility = View.VISIBLE
            holder.brand.text = "Brand: ${item.brand ?: "N/A"}"
            holder.meta.text = "Stock: ${item.stock ?: 0}"
            holder.meta.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.status_green_text))
        } else {
            holder.brand.visibility = View.GONE
            holder.meta.text = "Duration: ${item.duration ?: "N/A"}"
            holder.meta.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.text_gray))
        }

        holder.btnDelete.setOnClickListener { onDeleteClick(item) }

        holder.switchActive.setOnCheckedChangeListener { _, isChecked ->
            onToggleClick(item, isChecked)
        }
    }

    override fun getItemCount() = items.size
}