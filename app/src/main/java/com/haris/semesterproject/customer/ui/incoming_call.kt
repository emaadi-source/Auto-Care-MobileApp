package com.haris.semesterproject.customer.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.haris.semesterproject.R
import com.haris.semesterproject.network.Api
import com.haris.semesterproject.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class incoming_call : AppCompatActivity() {

    private lateinit var tvCallerName: TextView
    private lateinit var btnAccept: ImageView
    private lateinit var btnDecline: ImageView

    private var callId: Int = -1
    private var callerId: Int? = null
    private var receiverId: String? = null
    private var status: String? = null
    private var channelName: String? = null
    private var token: String? = null
    private var callType: String? = null
    private var createdAt: String? = null

    private lateinit var api: Api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call)

        tvCallerName = findViewById(R.id.tvCallerName)
        btnAccept = findViewById(R.id.btnAcceptCall)
        btnDecline = findViewById(R.id.btnDeclineCall)

        api = RetrofitClient.retrofit.create(Api::class.java)

        // 🔍 DEBUG: Log all intent extras
        Log.d("incoming_call", "=== Intent Extras Debug ===")
        intent.extras?.keySet()?.forEach { key ->
            Log.d("incoming_call", "Key: $key = ${intent.extras?.get(key)}")
        }

        // Get call data from intent (keys must match service!)
        // Do NOT use `val` here
        callId = intent.getIntExtra("id", -1)
        callerId = intent.getIntExtra("caller_id", -1)
        receiverId = intent.getStringExtra("receiver_id")
        status = intent.getStringExtra("status")

        status = intent.getStringExtra("status")

        // Validate call data
        if (callId == -1 || callerId == -1 || receiverId.isNullOrEmpty() ) {
            val errorMsg = "Invalid call data - ID: $callId, Caller: $callerId, Receiver: $receiverId"
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            finish()
            return
        }

        tvCallerName.text = "Incoming call from User $callerId"

        btnAccept.setOnClickListener {
            updateCallStatus("active") { // accepted
                val intent = Intent(this, Call_page::class.java).apply {
                    putExtra("id", callId)
                    putExtra("caller_id", callerId)
                    putExtra("receiver_id", receiverId)
                    putExtra("isIncoming", true)
                }
                startActivity(intent)
                finish()
            }
        }

        btnDecline.setOnClickListener {
            updateCallStatus("ended") { // declined
                Toast.makeText(this, "Call declined", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    }

    private fun updateCallStatus(status: String, onSuccess: (() -> Unit)? = null) {
        if (callId == -1 || callerId == -1) {
            Toast.makeText(this, "Invalid call ID or caller ID", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("incoming_call", "Updating call status to: $status for callerId: $callerId")

        api.updateCallStatus(
            action = "update",
            callId = callId.toString(),  // keep caller_id as per backend
            status = status
        ).enqueue(object : Callback<Api.CallResponse> {
            override fun onResponse(call: Call<Api.CallResponse>, response: Response<Api.CallResponse>) {
                val body = response.body()
                if (response.isSuccessful && body?.success == true) {
                    Log.d("incoming_call", "✅ Call status updated to $status")
                    onSuccess?.invoke()
                } else {
                    Log.e("incoming_call", "❌ Failed to update status: ${body?.message ?: response.message()}")
                    Toast.makeText(this@incoming_call, "Failed to update call status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Api.CallResponse>, t: Throwable) {
                Log.e("incoming_call", "❌ Update status error: ${t.message}")
                Toast.makeText(this@incoming_call, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    override fun onDestroy() {
        super.onDestroy()
        Log.d("incoming_call", "Activity destroyed")
    }
}