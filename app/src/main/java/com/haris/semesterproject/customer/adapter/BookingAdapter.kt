package com.haris.semesterproject.customer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.R
import com.haris.semesterproject.customer.data.NewBooking

class BookingAdapter(private val bookings: MutableList<NewBooking>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workshopName: TextView = itemView.findViewById(R.id.tvWorkshopName)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
        val date: TextView = itemView.findViewById(R.id.tvDate)// fixed
        val price: TextView = itemView.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bookingwhole, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.workshopName.text = booking.workshopName
        holder.date.text = "${booking.date} • ${booking.time}"
        holder.price.text = booking.price
        holder.status.text = booking.status.capitalize()

        val statusDrawable = when (booking.status.lowercase()) {
            "completed" -> R.drawable.status_completed
            "pending" -> R.drawable.status_pending
            else -> R.drawable.status_pending
        }
        holder.status.setBackgroundResource(statusDrawable)

        // Navigate button click
        holder.itemView.findViewById<Button>(R.id.navigateButton).setOnClickListener {
            val address = booking.address ?: ""
            val city = booking.city ?: ""
            val location = "$address, $city"
            val gmmIntentUri = android.net.Uri.parse("google.navigation:q=${location.replace(" ", "+")}")
            val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(holder.itemView.context.packageManager) != null) {
                holder.itemView.context.startActivity(mapIntent)
            } else {
                android.widget.Toast.makeText(holder.itemView.context, "Google Maps not installed", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun getItemCount(): Int = bookings.size
}
