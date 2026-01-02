package com.haris.semesterproject.utils

import com.haris.semesterproject.R

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.haris.semesterproject.customer.data.CustomerModule
import com.haris.semesterproject.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback

class ProviderFCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val providerId = SessionManager(this).fetchUserId()

        FirebaseFirestore.getInstance()
            .collection("providers")
            .document(providerId.toString())
            .update("fcmToken", token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: "New Booking"
        val message = remoteMessage.notification?.body ?: "You have received a new booking"

        showNotification(title, message)
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "provider_notifications"
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Booking Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        manager.notify(1, notification)
    }
}
