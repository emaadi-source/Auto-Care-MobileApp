package com.haris.semesterproject.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // send token to your server if you have one
        // e.g., sendTokenToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // If message contains a data payload, you can handle it here:
        remoteMessage.data.isNotEmpty().let {
            val data = remoteMessage.data
            // process data...
        }

        // If message contains notification payload, build a local notification:
        remoteMessage.notification?.let {
            val title = it.title ?: "New message"
            val body = it.body ?: ""
            showNotification(title, body)
        }
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "default_channel_id"
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Notification Channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        nm.notify(System.currentTimeMillis().toInt(), notification)
    }
}
