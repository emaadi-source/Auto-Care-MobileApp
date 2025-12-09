package com.haris.semesterproject.customer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.customer.data.Booking
import com.haris.semesterproject.R

class BookingAdapter(private val bookings: List<Booking>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workshopName: TextView = itemView.findViewById(R.id.tvWorkshopName)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val services: TextView = itemView.findViewById(R.id.tvServices)
        val price: TextView = itemView.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bookingwhole, parent, false)
        return BookingViewHolder(view)
    }

    //Fucntion to bind the view holder
    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.workshopName.text = booking.workshopName
        holder.status.text = booking.status
        holder.date.text = "${booking.date} • ${booking.time}"
        holder.services.text = booking.services.joinToString(", ")
        holder.price.text = booking.price
    }

    override fun getItemCount(): Int = bookings.size
}
