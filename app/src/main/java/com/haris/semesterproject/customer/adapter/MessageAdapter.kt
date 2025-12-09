package com.haris.semesterproject.customer.adapter

import android.app.AlertDialog
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.R
import com.haris.semesterproject.customer.data.ChatMessage
import com.squareup.picasso.Picasso

class MessageAdapter(
    private val messages: MutableList<ChatMessage>,
    private val currentUserId: String,
    private val baseUrl: String
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    var onDelete: ((ChatMessage) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_chat_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    fun removeMessage(position: Int) {
        messages.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.textMessage)
        private val imageView: ImageView = itemView.findViewById(R.id.imageMessage)
        private val container: LinearLayout = itemView.findViewById(R.id.messageContainer)
        private val editedIndicator: TextView = TextView(itemView.context).apply {
            textSize = 10f
            alpha = 0.6f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.END; setMargins(0, 0, 8, 0) }
            container.addView(this)
        }

        fun bind(msg: ChatMessage) {

            // --- Deleted Message ---
            if (msg.is_deleted) {
                messageText.text = if (msg.sender_id == currentUserId) "You deleted this message." else "This message was deleted."
                messageText.visibility = View.VISIBLE
                imageView.visibility = View.GONE
                editedIndicator.visibility = View.GONE
                messageText.setBackgroundColor(Color.parseColor("#E0E0E0"))
                messageText.setTextColor(Color.GRAY)
                container.gravity = if (msg.sender_id == currentUserId) Gravity.END else Gravity.START
                itemView.setOnLongClickListener(null)
                return
            }

            // Reset views
            messageText.visibility = View.GONE
            imageView.visibility = View.GONE
            messageText.setTextColor(Color.BLACK)

            // Image Message
            if (!msg.image_path.isNullOrEmpty()) {
                imageView.visibility = View.VISIBLE
                Picasso.get()
                    .load(msg.image_path)
                    .placeholder(R.drawable.default_user)
                    .error(R.drawable.default_user)
                    .into(imageView)
            }

            // Text Message
            if (!msg.message_content.isNullOrEmpty()) {
                messageText.text = msg.message_content
                messageText.visibility = View.VISIBLE
            }

            // Edited indicator
            if (msg.is_edited) {
                editedIndicator.text = "(edited)"
                editedIndicator.visibility = View.VISIBLE
            } else {
                editedIndicator.visibility = View.GONE
            }

            // Alignment & Background
            if (msg.sender_id == currentUserId) {
                container.gravity = Gravity.END
                if (!msg.is_deleted) messageText.setBackgroundResource(R.drawable.message_card_right)
            } else {
                container.gravity = Gravity.START
                if (!msg.is_deleted) messageText.setBackgroundResource(R.drawable.message_card_left)
            }

            // Long press for delete
            if (msg.sender_id == currentUserId && !msg.is_deleted) {
                itemView.setOnLongClickListener {
                    // Toast the message_id immediately
                    Toast.makeText(itemView.context, "Message ID: ${msg.message_id}", Toast.LENGTH_SHORT).show()
                    // Show delete option
                    showDeleteOption(msg)
                    true
                }
            } else {
                itemView.setOnLongClickListener(null)
            }
        }

        private fun showDeleteOption(msg: ChatMessage) {
            AlertDialog.Builder(itemView.context)
                .setTitle("Select Action")
                .setItems(arrayOf("Delete")) { _, _ ->
                    onDelete?.invoke(msg)
                }
                .show()
        }
    }
}
