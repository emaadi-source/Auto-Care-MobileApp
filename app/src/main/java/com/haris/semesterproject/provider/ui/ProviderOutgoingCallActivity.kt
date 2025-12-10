package com.haris.semesterproject.provider.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import java.util.*

class ProviderOutgoingCallActivity : AppCompatActivity() {

    private lateinit var tvCalleeName: TextView
    private lateinit var btnCancelCall: ImageView

    private var receiverId: String? = null
    private var currentUserId: String = ""
    private var receiverName: String = "Customer"
    private var callIdFromServer: Int? = null

    private val handler = Handler(Looper.getMainLooper())
    private val pollDelay = 1500L // Check status every 1.5 seconds

    private lateinit var api: Api

    private val pollRunnable = object : Runnable {
        override fun run() {
            pollCallStatus()
            handler.postDelayed(this, pollDelay)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_outgoing_call) // Ensure this XML exists

        tvCalleeName = findViewById(R.id.tvCalleeName)
        btnCancelCall = findViewById(R.id.btnCancelCall)

        // Get data passed from Chat or Booking Details
        receiverId = intent.getStringExtra("receiverId")
        currentUserId = intent.getStringExtra("current_user_id").toString()
        receiverName = intent.getStringExtra("receiver_name") ?: "Customer"

        // Set the name of the person we are calling
        tvCalleeName.text = "Calling $receiverName..."

        api = RetrofitClient.retrofit.create(Api::class.java)

        if (currentUserId.isEmpty() || receiverId.isNullOrEmpty()) {
            Toast.makeText(this, "Error: Missing user info", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnCancelCall.setOnClickListener { cancelCall() }

        createCallOnServer()
    }

    private fun createCallOnServer() {
        api.createCall(
            callerId = currentUserId,
            receiverId = receiverId!!
        ).enqueue(object : Callback<Api.CallResponse> {
            override fun onResponse(call: Call<Api.CallResponse>, response: Response<Api.CallResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    callIdFromServer = response.body()?.call?.id
                    Log.d("PROVIDER_CALL", "Call created with ID: $callIdFromServer")
                    // Start checking if they accepted
                    handler.post(pollRunnable)
                } else {
                    Toast.makeText(this@ProviderOutgoingCallActivity, "Failed to connect call", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<Api.CallResponse>, t: Throwable) {
                Log.e("PROVIDER_CALL", "Create call error: ${t.message}")
                Toast.makeText(this@ProviderOutgoingCallActivity, "Network error", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun pollCallStatus() {
        callIdFromServer?.let { callId ->
            api.getCallStatus(callId = callId)
                .enqueue(object : Callback<Api.CallStatusResponse> {
                    override fun onResponse(
                        call: Call<Api.CallStatusResponse>,
                        response: Response<Api.CallStatusResponse>
                    ) {
                        val body = response.body()
                        if (!response.isSuccessful || body?.success != true || body.call == null) return

                        val status = body.call.status ?: "ringing"
                        Log.d("PROVIDER_CALL", "Polled status: $status")

                        when (status.lowercase(Locale.getDefault())) {
                            "active" -> {
                                // Customer Accepted!
                                handler.removeCallbacks(pollRunnable)
                                goToCallScreen()
                            }
                            "ended", "rejected" -> {
                                // Customer Declined
                                handler.removeCallbacks(pollRunnable)
                                Toast.makeText(this@ProviderOutgoingCallActivity, "Call ended", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    }

                    override fun onFailure(call: Call<Api.CallStatusResponse>, t: Throwable) {
                        Log.e("PROVIDER_CALL", "Poll error: ${t.message}")
                    }
                })
        }
    }

    private fun goToCallScreen() {
        // Navigate to the actual Provider Call Screen
        val intent = Intent(this, ProviderCallActivity::class.java).apply {
            putExtra("callId", callIdFromServer)
            putExtra("callerId", currentUserId)
            putExtra("receiverId", receiverId)
            putExtra("isIncoming", false)
        }
        startActivity(intent)
        finish()
    }

    private fun cancelCall() {
        // If the provider hangs up before the customer answers
        callIdFromServer?.let { _ ->
            api.updateCallStatus(
                action = "update",
                callId = currentUserId, // Using caller_id key as per your backend logic
                status = "ended"
            ).enqueue(object : Callback<Api.CallResponse> {
                override fun onResponse(call: Call<Api.CallResponse>, response: Response<Api.CallResponse>) {
                    handler.removeCallbacks(pollRunnable)
                    Toast.makeText(this@ProviderOutgoingCallActivity, "Call cancelled", Toast.LENGTH_SHORT).show()
                    finish()
                }

                override fun onFailure(call: Call<Api.CallResponse>, t: Throwable) {
                    handler.removeCallbacks(pollRunnable)
                    finish()
                }
            })
        } ?: run {
            handler.removeCallbacks(pollRunnable)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(pollRunnable)
    }
}