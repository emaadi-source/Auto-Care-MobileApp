package com.haris.semesterproject.provider.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.R
import com.haris.semesterproject.provider.data.EarningTransaction

class EarningsAdapter(private var transactions: List<EarningTransaction>) :
    RecyclerView.Adapter<EarningsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCustomer: TextView = view.findViewById(R.id.tvCustomerService)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_earning, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = transactions[position]
        holder.tvCustomer.text = "${item.service} • ${item.customer}"
        holder.tvDate.text = item.date
        holder.tvAmount.text = "+ ₹${item.amount}"
    }

    override fun getItemCount() = transactions.size

    fun updateData(newList: List<EarningTransaction>) {
        transactions = newList
        notifyDataSetChanged()
    }
}