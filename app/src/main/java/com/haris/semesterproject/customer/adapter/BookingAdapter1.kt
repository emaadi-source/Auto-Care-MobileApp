package com.haris.semesterproject.customer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.R
import com.haris.semesterproject.customer.data.NewBooking

class BookingAdapter1(bookings: List<NewBooking>) :
    RecyclerView.Adapter<BookingAdapter1.BookingViewHolder>() {

    // Only keep bookings that are pending
    private val pendingBookings = bookings.filter { it.status.lowercase() == "pending" }.toMutableList()

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workshopName: TextView = itemView.findViewById(R.id.tvWorkshopName)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val price: TextView = itemView.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_current_status, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = pendingBookings[position]
        holder.workshopName.text = booking.workshopName
        holder.date.text = "${booking.date} • ${booking.time}"
        holder.price.text = booking.price

        // Status badge color
        holder.status.text = booking.status.capitalize()
        val statusDrawable = when (booking.status.lowercase()) {
            "completed" -> R.drawable.status_completed
            "pending" -> R.drawable.status_pending
            else -> R.drawable.status_pending
        }
        holder.status.setBackgroundResource(statusDrawable)
    }

    override fun getItemCount(): Int = pendingBookings.size
}
