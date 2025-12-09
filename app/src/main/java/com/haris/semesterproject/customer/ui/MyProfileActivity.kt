package com.haris.semesterproject.customer.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.haris.semesterproject.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class MyProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val btnServiceHistory = findViewById<MaterialButton>(R.id.btnServiceHistory)
        val btnMyBookings = findViewById<MaterialButton>(R.id.btnMyBookings)

        btnServiceHistory.setOnClickListener {
            startActivity(Intent(this, ServiceHistoryActivity::class.java))
        }

        btnMyBookings.setOnClickListener {
            startActivity(Intent(this, MyBookingsActivity::class.java))
        }
    }
}
