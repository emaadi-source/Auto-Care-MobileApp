package com.haris.semesterproject.provider.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.R
import com.haris.semesterproject.provider.data.BookingDetails

class IncomingBookingAdapter(
    private var bookings: List<BookingDetails>,
    private val onAccept: (BookingDetails) -> Unit,
    private val onDecline: (BookingDetails) -> Unit,
    private val onClick: (BookingDetails) -> Unit
) : RecyclerView.Adapter<IncomingBookingAdapter.Holder>() {

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvCustomerName)
        val details: TextView = itemView.findViewById(R.id.tvServiceAndBike)
        val price: TextView = itemView.findViewById(R.id.tvPrice)
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        val btnDecline: Button = itemView.findViewById(R.id.btnDecline)
        val layoutActions: View = itemView.findViewById(R.id.layoutActions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_incoming_booking, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = bookings[position]
        holder.name.text = item.customer_name
        holder.details.text = "${item.main_service ?: "Service"} • ${item.vehicle_model ?: "Bike"}"
        holder.price.text = "Rs ${item.total}"
        holder.date.text = item.booking_date

        // Hide buttons if already confirmed
        if (item.status == "confirmed") {
            holder.layoutActions.visibility = View.GONE
        } else {
            holder.layoutActions.visibility = View.VISIBLE
        }

        holder.btnAccept.setOnClickListener { onAccept(item) }
        holder.btnDecline.setOnClickListener { onDecline(item) }
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = bookings.size

    fun updateList(newList: List<BookingDetails>) {
        bookings = newList
        notifyDataSetChanged()
    }
}