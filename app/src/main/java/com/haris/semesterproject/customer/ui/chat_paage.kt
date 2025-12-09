package com.haris.semesterproject.customer.ui

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.R
import com.haris.semesterproject.customer.adapter.MessageAdapter
import com.haris.semesterproject.customer.data.ChatMessage
import com.haris.semesterproject.network.Api
import com.haris.semesterproject.network.RetrofitClient
import com.haris.semesterproject.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class chat_paage : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageView
    private lateinit var chatAdapter: MessageAdapter
    private val messageList = mutableListOf<ChatMessage>()

    private var senderId: Int = 0
    private var receiverId: Int = 0
    private lateinit var api: Api
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_paage)

        sessionManager = SessionManager(this)
        senderId = sessionManager.fetchUserId()
        if (senderId <= 0) {
            Toast.makeText(this, "Invalid sender ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        receiverId = intent.getIntExtra("providerId", 0)
        if (receiverId <= 0) {
            Toast.makeText(this, "Receiver ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val callbtn = findViewById<ImageView>(R.id.audio_call)
        callbtn.setOnClickListener {
            val intent = Intent(this, outgoing_call::class.java)
            intent.putExtra("receiverId", receiverId.toString()) // fixed typo
            intent.putExtra("current_user_id", senderId.toString()) // send as String
            startActivity(intent)
        }

        val backbtn =findViewById<ImageView>(R.id.back_icon)
        backbtn.setOnClickListener {
            finish()
        }


        api = RetrofitClient.retrofit.create(Api::class.java)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageEditText = findViewById(R.id.s1)
        sendButton = findViewById(R.id.v1)

        chatAdapter = MessageAdapter(messageList, senderId.toString(), RetrofitClient.BASE_URL)
        chatAdapter.onDelete = { message -> deleteMessage(message) }
        chatRecyclerView.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        chatRecyclerView.adapter = chatAdapter

        sendButton.setOnClickListener { sendMessage() }

        fetchMessages()
    }

    private fun fetchMessages() {
        api.getMessages(senderId, receiverId).enqueue(object : Callback<List<ChatMessage>> {
            override fun onResponse(call: Call<List<ChatMessage>>, response: Response<List<ChatMessage>>) {
                if (response.isSuccessful && response.body() != null) {
                    val messages = response.body()!!
                    messageList.clear()
                    messageList.addAll(messages)
                    chatAdapter.notifyDataSetChanged()
                    if (messageList.isNotEmpty()) {
                        chatRecyclerView.scrollToPosition(messageList.size - 1)
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    android.util.Log.e("ChatPage", "Response not successful: $errorBody")
                    Toast.makeText(this@chat_paage, "Failed to load messages", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ChatMessage>>, t: Throwable) {
                android.util.Log.e("ChatPage", "Failed to load messages", t)
                Toast.makeText(this@chat_paage, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
    private fun sendMessage() {
        val text = messageEditText.text.toString().trim()
        if (text.isEmpty()) return

        val message = ChatMessage(
            message_id = UUID.randomUUID().toString(), // temporary until server responds
            sender_id = senderId.toString(),
            receiver_id = receiverId.toString(),
            message_content = text,
            image_path = null,
            timestamp = System.currentTimeMillis(),
            is_edited = false,
            is_deleted = false,
            vanish_mode = false
        )

        // Optimistic UI update
        messageList.add(message)
        chatAdapter.notifyItemInserted(messageList.size - 1)
        chatRecyclerView.scrollToPosition(messageList.size - 1)
        messageEditText.setText("")

        api.sendMessage(senderId.toString(), receiverId.toString(), text)
            .enqueue(object : Callback<Api.ApiResponse> {
                override fun onResponse(call: Call<Api.ApiResponse>, response: Response<Api.ApiResponse>) {
                    val body = response.body()
                    if (response.isSuccessful && body?.success == true) {
                        // Update the last inserted message with server-generated values
                        val idx = messageList.indexOfLast { it.message_id == message.message_id }
                        if (idx != -1) {
                            val updated = message.copy(
                                message_id = body.message_id ?: message.message_id,
                                timestamp = body.timestamp ?: message.timestamp,
                                vanish_mode = body.vanish_mode ?: false,
                                image_path = body.image_path ?: message.image_path
                            )
                            messageList[idx] = updated
                            chatAdapter.notifyItemChanged(idx)
                        } else {
                            chatAdapter.notifyDataSetChanged()
                        }
                    } else {
                        // Roll back optimistic UI
                        messageList.removeLastOrNull()
                        chatAdapter.notifyDataSetChanged()
                        Toast.makeText(this@chat_paage, "Failed to send message", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Api.ApiResponse>, t: Throwable) {
                    // Roll back optimistic UI
                    messageList.removeLastOrNull()
                    chatAdapter.notifyDataSetChanged()
                    Toast.makeText(this@chat_paage, "Failed to send message: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun deleteMessage(message: ChatMessage) {
        api.deleteMessage(message.message_id).enqueue(object : Callback<Api.ApiResponse> {
            override fun onResponse(call: Call<Api.ApiResponse>, response: Response<Api.ApiResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    // Find the message by ID
                    val index = messageList.indexOfFirst { it.message_id == message.message_id }
                    Toast.makeText(this@chat_paage, "$index", Toast.LENGTH_SHORT).show()
                    if (index != -1) {
                        messageList.removeAt(index)
                        chatAdapter.notifyItemRemoved(index)
                        Toast.makeText(this@chat_paage, "Message deleted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@chat_paage, "Failed to delete message", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Api.ApiResponse>, t: Throwable) {
                Toast.makeText(this@chat_paage, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
