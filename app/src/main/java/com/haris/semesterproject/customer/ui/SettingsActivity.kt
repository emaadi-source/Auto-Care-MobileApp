package com.haris.semesterproject.customer.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.haris.semesterproject.R
import com.haris.semesterproject.authentication.LoginActivity
import com.haris.semesterproject.utils.SessionManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sessionManager = SessionManager(this)

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)
        val btnEditProfile = findViewById<MaterialButton>(R.id.edtprofile)

        val tvFullName = findViewById<TextView>(R.id.tvFullName)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)

        // Fetch current user info from SessionManager
        tvFullName.text = sessionManager.fetchUserName()
        tvEmail.text = sessionManager.fetchUserEmail()

        btnLogout.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("logout", true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
