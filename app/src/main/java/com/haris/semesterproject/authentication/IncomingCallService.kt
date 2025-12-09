package com.haris.semesterproject.authentication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.haris.semesterproject.R
import com.haris.semesterproject.customer.ui.incoming_call
import com.haris.semesterproject.network.RetrofitClient
import com.haris.semesterproject.network.Api
import com.haris.semesterproject.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class IncomingCallService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private val pollingDelay = 2000L // 2 seconds

    private lateinit var sessionManager: SessionManager

    private var userId: String? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startForeground(
            1,
            createNotification()
        )

        sessionManager = SessionManager(this)
        userId = sessionManager.fetchUserId().toString()

        startPolling()
        return START_STICKY
    }

    private fun createNotification(): Notification {
        val channelId = "incoming_call_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Incoming Call Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Checking for incoming calls")
            .setContentText("Waiting for incoming calls…")
            .setSmallIcon(R.drawable.ic_call)
            .build()
    }


    /** 🔁 Start polling every 2 seconds */
    private fun startPolling() {
        handler.post(object : Runnable {
            override fun run() {
                checkIncomingCall()
                handler.postDelayed(this, pollingDelay)
            }
        })
    }

    private fun checkIncomingCall() {
        Log.d("IncomingCallService", "🔍 Checking for calls… userId=$userId")

        RetrofitClient.api.getIncomingCall(receiverId = userId!!)
            .enqueue(object : Callback<Api.CallResponse> {
                override fun onResponse(call: Call<Api.CallResponse>, response: Response<Api.CallResponse>) {
                    val body = response.body()
                    if (body?.success != true || body.call == null) {
                        Log.d("IncomingCallService", "ℹ No incoming calls.")
                        return
                    }

                    val callData = body.call
                    Log.d("IncomingCallService", "📞 Incoming Call → Caller: ${callData.caller_id}, Call ID: ${callData.id}")

                    // Immediately mark as pending so it won't trigger again
                    updateCallStatusToPending(callData.id) {
                        openIncomingCallScreen(callData)
                    }
                }

                override fun onFailure(call: Call<Api.CallResponse>, t: Throwable) {
                    Log.e("IncomingCallService", "❌ Network Error: ${t.message}")
                }
            })
    }

    private fun updateCallStatusToPending(callId: Int, onSuccess: (() -> Unit)? = null) {
        RetrofitClient.api.updateCallStatus(
            action = "update",
            callId = callId.toString(),
            status = "pending" // now valid
        ).enqueue(object : Callback<Api.CallResponse> {
            override fun onResponse(
                call: Call<Api.CallResponse>,
                response: Response<Api.CallResponse>
            ) {
                val body = response.body()
                if (body?.success == true) {
                    Log.d("IncomingCallService", "Call ID $callId marked as pending")
                    onSuccess?.invoke()
                } else {
                    Log.e(
                        "IncomingCallService",
                        "Failed to mark pending: ${body?.message ?: response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<Api.CallResponse>, t: Throwable) {
                Log.e("IncomingCallService", "Failed to mark pending: ${t.message}")
            }
        })
    }




    /** 🎬 Launch Incoming Call Activity */
    private fun openIncomingCallScreen(call: Api.CallData) {
       // Toast.makeText(this, "📞 Incoming Call from ${call.caller_id}", Toast.LENGTH_LONG).show()

        val intent = Intent(this, incoming_call::class.java).apply {
            putExtra("id", call.id)
            putExtra("caller_id", call.caller_id)
            putExtra("receiver_id", userId)
            putExtra("status", call.status)


            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        Log.d("IncomingCallService", "🎬 Opening IncomingCallActivity…")

        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        Log.d("IncomingCallService", "🛑 Service stopped.")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
