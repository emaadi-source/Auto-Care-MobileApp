package com.haris.semesterproject.provider.ui

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.R
import com.haris.semesterproject.customer.data.ChatMessage
import com.haris.semesterproject.network.Api
import com.haris.semesterproject.network.RetrofitClient
import com.haris.semesterproject.provider.adapter.ProviderMessageAdapter
import com.haris.semesterproject.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ProviderChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageView
    private lateinit var chatAdapter: ProviderMessageAdapter
    private val messageList = mutableListOf<ChatMessage>()

    private var currentProviderId: Int = 0
    private var customerId: Int = 0
    private var customerName: String = "Customer"

    private lateinit var api: Api
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_chat)

        sessionManager = SessionManager(this)
        currentProviderId = sessionManager.fetchUserId()

        // Get Customer Details from Intent
        customerId = intent.getIntExtra("customer_id", 0)
        customerName = intent.getStringExtra("customer_name") ?: "Customer"

        if (currentProviderId == -1 || customerId == 0) {
            Toast.makeText(this, "Error: Invalid IDs", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        setupRecyclerView()

        api = RetrofitClient.retrofit.create(Api::class.java)
        fetchMessages()
    }

    private fun setupUI() {
        val tvTitle = findViewById<TextView>(R.id.tvChatTitle)
        tvTitle.text = customerName

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        // Audio Call Button
        findViewById<ImageView>(R.id.btnAudioCall).setOnClickListener {
            val intent = Intent(this, ProviderOutgoingCallActivity::class.java)
            intent.putExtra("receiverId", customerId.toString())
            intent.putExtra("current_user_id", currentProviderId.toString())
            intent.putExtra("receiver_name", customerName)
            startActivity(intent)
        }

        messageEditText = findViewById(R.id.etMessage)
        sendButton = findViewById(R.id.btnSend)

        sendButton.setOnClickListener { sendMessage() }
    }

    private fun setupRecyclerView() {
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        // Pass currentProviderId as string to match ChatMessage data class
        chatAdapter = ProviderMessageAdapter(messageList, currentProviderId.toString())

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        chatRecyclerView.layoutManager = layoutManager
        chatRecyclerView.adapter = chatAdapter
    }

    private fun fetchMessages() {
        // Note: Logic is same, just IDs are swapped in context
        api.getMessages(currentProviderId, customerId).enqueue(object : Callback<List<ChatMessage>> {
            override fun onResponse(call: Call<List<ChatMessage>>, response: Response<List<ChatMessage>>) {
                if (response.isSuccessful && response.body() != null) {
                    messageList.clear()
                    messageList.addAll(response.body()!!)
                    chatAdapter.notifyDataSetChanged()
                    if (messageList.isNotEmpty()) {
                        chatRecyclerView.scrollToPosition(messageList.size - 1)
                    }
                }
            }
            override fun onFailure(call: Call<List<ChatMessage>>, t: Throwable) {
                // Fail silently or show error
            }
        })
    }

    private fun sendMessage() {
        val text = messageEditText.text.toString().trim()
        if (text.isEmpty()) return

        val tempId = UUID.randomUUID().toString()
        val message = ChatMessage(
            message_id = tempId,
            sender_id = currentProviderId.toString(),
            receiver_id = customerId.toString(),
            message_content = text,
            timestamp = System.currentTimeMillis()
        )

        // UI Update
        messageList.add(message)
        chatAdapter.notifyItemInserted(messageList.size - 1)
        chatRecyclerView.scrollToPosition(messageList.size - 1)
        messageEditText.setText("")

        // API Call
        api.sendMessage(currentProviderId.toString(), customerId.toString(), text)
            .enqueue(object : Callback<Api.ApiResponse> {
                override fun onResponse(call: Call<Api.ApiResponse>, response: Response<Api.ApiResponse>) {
                    if (!response.isSuccessful) {
                        Toast.makeText(applicationContext, "Failed to send", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Api.ApiResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Network Error", Toast.LENGTH_SHORT).show()
                }
            })
    }
}