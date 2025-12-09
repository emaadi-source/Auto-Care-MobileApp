package com.haris.semesterproject.customer.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.haris.semesterproject.R
import com.haris.semesterproject.authentication.LoginActivity
import com.haris.semesterproject.utils.SessionManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize Session Manager
        sessionManager = SessionManager(this)

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        btnLogout.setOnClickListener {
            // 1. Clear User Session
            sessionManager.logout()

            // 2. Navigate to Login Screen
            val intent = Intent(this, LoginActivity::class.java)

            // 3. Add flags to clear the back stack (so user can't press back to return)
            intent.putExtra("logout", true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }

        // Handle Edit button click to open EditProfile
        val btnEditProfile = findViewById<MaterialButton>(R.id.edtprofile)
        btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        // Toggle switches (optional)
        findViewById<SwitchMaterial>(R.id.switchBookingUpdates).isChecked = true
        findViewById<SwitchMaterial>(R.id.switchServiceReminders).isChecked = true
        findViewById<SwitchMaterial>(R.id.switchOffers).isChecked = true
    }
}