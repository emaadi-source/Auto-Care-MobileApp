package com.haris.semesterproject.provider.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.databinding.ItemWorkingHourBinding
import com.haris.semesterproject.provider.data.WorkingHour

class WorkingHoursAdapter(private val hours: List<WorkingHour>) :
    RecyclerView.Adapter<WorkingHoursAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemWorkingHourBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hour: WorkingHour) {
            binding.tvDay.text = hour.day
            binding.tvTime.text = hour.time
            binding.switchDay.isChecked = hour.isOpen
            binding.tvTime.alpha = if (hour.isOpen) 1f else 0.5f
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWorkingHourBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(hours[position])
    }

    override fun getItemCount() = hours.size
}