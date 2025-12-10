package com.haris.semesterproject.provider.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.haris.semesterproject.R
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine

class ProviderCallActivity : AppCompatActivity() {

    private var mRtcEngine: RtcEngine? = null
    // MATCH THESE EXACTLY WITH CUSTOMER APP
    private val APP_ID = "32c73f93ca5b455792c129934e6aa241"
    private val TOKEN = "007eJxTYNC+UjOj/6+T/W2RGJFFEYZs/oLxwTI37n9/e67fLL9SfIkCg7FRsrlxmqVxcqJpkompqbmlUbKhkaWlsUmqWWKikYlh80+jzIZARgZl4TUMjFAI4rMzlKQWlxgaGTMwAAAy1h4g"
    private val CHANNEL_NAME = "test123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_call)

        if (checkPermission()) {
            initAgora()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 101)
        }

        findViewById<ImageView>(R.id.btnEndCall).setOnClickListener {
            leaveChannel()
        }
    }

    private fun initAgora() {
        try {
            mRtcEngine = RtcEngine.create(baseContext, APP_ID, object : IRtcEngineEventHandler() {
                override fun onUserJoined(uid: Int, elapsed: Int) {
                    runOnUiThread { findViewById<TextView>(R.id.tvStatus).text = "Connected" }
                }
                override fun onUserOffline(uid: Int, reason: Int) {
                    runOnUiThread { leaveChannel() }
                }
            })
            mRtcEngine?.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
            mRtcEngine?.enableAudio()
            mRtcEngine?.joinChannel(TOKEN, CHANNEL_NAME, "", 0)
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun leaveChannel() {
        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
        mRtcEngine = null
        finish()
    }

    private fun checkPermission() = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
}