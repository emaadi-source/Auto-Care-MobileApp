package com.haris.semesterproject.customer.data

data class ChatMessage(
    val message_id: String,
    val sender_id: String,
    val receiver_id: String,
    val message_content: String? = null,
    val image_path: String? = null,
    val timestamp: Long? = null,
    val is_edited: Boolean = false,
    val is_deleted: Boolean = false,
    var vanish_mode: Boolean = false
)
