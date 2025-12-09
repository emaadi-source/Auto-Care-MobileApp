package com.haris.semesterproject.customer.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.haris.semesterproject.R
import com.google.android.material.appbar.MaterialToolbar

class EditProfileActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)


        // Back button in the toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBarEdit)
        toolbar.setNavigationOnClickListener {
            finish() // Close this activity and return to previous
        }
    }
}
