package com.haris.semesterproject.customer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.customer.adapter.ServiceHistoryAdapter
import com.haris.semesterproject.customer.data.ServiceHistory
import com.haris.semesterproject.R

class ServiceHistoryActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_history)

        val topAppBar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        rvHistory = findViewById(R.id.rvHistory)

        val historyList = listOf(
            ServiceHistory("BK005", "Mike's Garage", "Completed", "2024-10-05", "10:00 AM - 11:30 AM", "123 Workshop Street, City", listOf("Oil Change", "Brake Check", "Chain Cleaning"), "₹650", "Raj Kumar"),
            ServiceHistory("BK004", "Speedy Service", "Completed", "2024-09-28", "02:00 PM - 02:45 PM", "456 Service Road, City", listOf("Chain Cleaning", "Battery Check"), "₹250", "Rahul Mehta")
        )

        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = ServiceHistoryAdapter(historyList)
    }
}
