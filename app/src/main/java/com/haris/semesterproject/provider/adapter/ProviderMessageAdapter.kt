package com.haris.semesterproject.provider.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.R
import com.haris.semesterproject.customer.data.ChatMessage

class ProviderMessageAdapter(
    private val messages: List<ChatMessage>,
    private val currentUserId: String
) : RecyclerView.Adapter<ProviderMessageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textMessage: TextView = view.findViewById(R.id.textMessage)
        val container: LinearLayout = view.findViewById(R.id.messageContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_chat_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msg = messages[position]
        holder.textMessage.text = msg.message_content

        // Provider (Me) -> Right Side, Maroon Bubble
        if (msg.sender_id == currentUserId) {
            holder.container.gravity = Gravity.END
            holder.textMessage.setBackgroundResource(R.drawable.bg_bubble_provider) // Need to create this
            holder.textMessage.setTextColor(android.graphics.Color.WHITE)
        }
        // Customer (Them) -> Left Side, Grey Bubble
        else {
            holder.container.gravity = Gravity.START
            holder.textMessage.setBackgroundResource(R.drawable.bg_bubble_customer) // Need to create this
            holder.textMessage.setTextColor(android.graphics.Color.BLACK)
        }
    }

    override fun getItemCount() = messages.size
}