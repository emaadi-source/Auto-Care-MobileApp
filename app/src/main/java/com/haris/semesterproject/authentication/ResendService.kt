package com.haris.semesterproject.authentication

import android.app.IntentService
import android.content.Intent
import com.haris.semesterproject.customer.data.ChatMessage
import com.haris.semesterproject.customer.ui.MessageDatabaseHelper

class ResendService : IntentService("ResendService") {

    override fun onHandleIntent(intent: Intent?) {
        val db = MessageDatabaseHelper(this)
        val unsent = db.getUnsentMessages()  // all is_sent = 0

        for (msg in unsent) {
            val success = sendToServer(msg)
            if (success) {
                msg.vanish_mode = true
                db.updateSentFlag(msg.message_id!!)
            }
        }
    }

    private fun sendToServer(msg: ChatMessage): Boolean {
        // TODO: your api call
        return true
    }
}
