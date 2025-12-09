package com.haris.semesterproject.customer.ui

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
class outgoing_call : AppCompatActivity() {

    private lateinit var tvCalleeName: TextView
    private lateinit var btnCancelCall: ImageView

    private var receiverId: String? = null
    private var currentUserId: String = ""
    private var callIdFromServer: Int? = null

    private val handler = Handler(Looper.getMainLooper())
    private val pollDelay = 1500L

    private lateinit var api: Api

    private val pollRunnable = object : Runnable {
        override fun run() {
            pollCallStatus() // no argument needed
            handler.postDelayed(this, pollDelay)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outgoing_call)

        tvCalleeName = findViewById(R.id.tvCalleeName)
        btnCancelCall = findViewById(R.id.btnCancelCall)

        receiverId = intent.getStringExtra("receiverId")
        currentUserId = intent.getStringExtra("current_user_id").toString()

        tvCalleeName.text = "Calling.."

        api = RetrofitClient.retrofit.create(Api::class.java)

        if (currentUserId.isEmpty() || receiverId.isNullOrEmpty()) {
            Toast.makeText(this, "Missing user info", Toast.LENGTH_SHORT).show()
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
                    Log.d("CALL_DEBUG", "Call created with ID: $callIdFromServer")
                    handler.post(pollRunnable)
                } else {
                    Toast.makeText(this@outgoing_call, "Failed to create call", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<Api.CallResponse>, t: Throwable) {
                Log.e("CALL_DEBUG", "Create call error: ${t.message}")
                Toast.makeText(this@outgoing_call, "Network error", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun pollCallStatus() {
        callIdFromServer?.let { callId ->
            api.getCallStatus(callId = callId) // use the actual callId, not currentUserId
                .enqueue(object : Callback<Api.CallStatusResponse> {
                    override fun onResponse(
                        call: Call<Api.CallStatusResponse>,
                        response: Response<Api.CallStatusResponse>
                    ) {
                        val body = response.body()
                        if (!response.isSuccessful || body?.success != true || body.call == null) return

                        val status = body.call.status ?: "ringing"
                        Log.d("CALL_DEBUG", "Polled status: $status")

                        when (status.lowercase(Locale.getDefault())) {
                            "active" -> {
                                handler.removeCallbacks(pollRunnable)
                                goToCallScreen()
                            }
                            "ended" -> {
                                handler.removeCallbacks(pollRunnable)
                                Toast.makeText(this@outgoing_call, "Call ended", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    }

                    override fun onFailure(call: Call<Api.CallStatusResponse>, t: Throwable) {
                        Log.e("CALL_DEBUG", "Poll call error: ${t.message}")
                    }
                })
        }
    }



    private fun goToCallScreen() {
        val intent = Intent(this, Call_page::class.java).apply {
            putExtra("callId", callIdFromServer)
            putExtra("callerId", currentUserId)
            putExtra("receiverId", receiverId)
            putExtra("isIncoming", false)
        }
        startActivity(intent)
        finish()
    }

    private fun cancelCall() {
        // If call already exists on server, mark as ended
        callIdFromServer?.let { callId ->
            api.updateCallStatus(
                action = "update",
                callId = currentUserId, // keep using caller_id as per backend
                status = "ended"
            ).enqueue(object : Callback<Api.CallResponse> {
                override fun onResponse(call: Call<Api.CallResponse>, response: Response<Api.CallResponse>) {
                    handler.removeCallbacks(pollRunnable)
                    Toast.makeText(this@outgoing_call, "Call cancelled", Toast.LENGTH_SHORT).show()
                    finish()
                }

                override fun onFailure(call: Call<Api.CallResponse>, t: Throwable) {
                    handler.removeCallbacks(pollRunnable)
                    Toast.makeText(this@outgoing_call, "Network error while cancelling", Toast.LENGTH_SHORT).show()
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
