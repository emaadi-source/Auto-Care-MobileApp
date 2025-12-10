package com.haris.semesterproject.provider.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.R
import com.haris.semesterproject.provider.data.Booking

class BookingAdapter(private var bookings: List<Booking>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val service: TextView = view.findViewById(R.id.tv_service)
        val price: TextView = view.findViewById(R.id.tv_price)
        val status: TextView = view.findViewById(R.id.tv_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking_dashboard, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]

        holder.name.text = booking.name
        holder.service.text = booking.service
        holder.price.text = booking.price

        holder.status.text = booking.status.capitalize()

        // Color coding for status
        when (booking.status.lowercase()) {
            "pending" -> {
                holder.status.setBackgroundResource(R.drawable.bg_chip_orange)
                holder.status.setTextColor(Color.parseColor("#FF9800"))
            }
            "confirmed" -> {
                holder.status.setBackgroundResource(R.drawable.bg_chip_green)
                holder.status.setTextColor(Color.parseColor("#4CAF50"))
            }
            "completed" -> {
                holder.status.setBackgroundResource(R.drawable.bg_chip_gray)
                holder.status.setTextColor(Color.GRAY)
            }
            else -> {
                holder.status.setTextColor(Color.BLACK)
            }
        }
    }

    override fun getItemCount() = bookings.size

    fun updateBookings(newBookings: List<Booking>) {
        bookings = newBookings
        notifyDataSetChanged()
    }
}