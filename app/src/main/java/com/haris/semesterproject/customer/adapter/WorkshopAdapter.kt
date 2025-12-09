package com.haris.semesterproject.customer.adapter

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.customer.data.Workshop
import com.haris.semesterproject.R
import com.haris.semesterproject.customer.ui.WorkshopDetailActivity

class WorkshopAdapter(private var items: MutableList<Workshop>) :
    RecyclerView.Adapter<WorkshopAdapter.WorkshopViewHolder>() {

    private var originalList: MutableList<Workshop> = items.toMutableList()

    fun updateData(newList: List<Workshop>) {
        items.clear()
        items.addAll(newList)
        originalList.clear()
        originalList.addAll(newList)
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        items = if (query.isEmpty()) {
            originalList.toMutableList()
        } else {
            originalList.filter {
                it.name.contains(query, ignoreCase = true) || it.details.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    class WorkshopViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvName)
        val details: TextView = view.findViewById(R.id.tvDetails)
        val bookNow: Button = view.findViewById(R.id.btnBookNow)
        val image: ImageView = view.findViewById(R.id.imgWorkshop)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkshopViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workshop, parent, false)
        return WorkshopViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkshopViewHolder, position: Int) {
        val item = items[position]

        holder.name.text = item.name
        holder.details.text = item.details

        // Image Decoding
        try {
            val imageBytes = Base64.decode(item.image, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            holder.image.setImageBitmap(bitmap)
        } catch (e: Exception) {
            holder.image.setImageResource(R.drawable.sample_bike)
        }

        // Logic to open detail screen
        val openDetail = View.OnClickListener {
            Log.d("Adapter", "Clicking Workshop ID: ${item.id}")

            val context = holder.itemView.context
            val intent = Intent(context, WorkshopDetailActivity::class.java)
            intent.putExtra("WORKSHOP_PROVIDER_ID", item.id)
            intent.putExtra("WORKSHOP_NAME", item.name)
            intent.putExtra("WORKSHOP_DETAILS", item.details)
            intent.putExtra("WORKSHOP_IMAGE", item.image)
            context.startActivity(intent)
        }

        // Apply click listener to BOTH the button and the whole card
        holder.bookNow.setOnClickListener(openDetail)
        holder.itemView.setOnClickListener(openDetail)
    }

    override fun getItemCount(): Int = items.size
}