package com.haris.semesterproject.customer.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.customer.adapter.BookingAdapter
import com.haris.semesterproject.customer.data.Booking
import com.haris.semesterproject.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class MyBookingsActivity : AppCompatActivity() {

    private lateinit var rvBookings: RecyclerView
    private lateinit var btnBookNew: MaterialButton
    private lateinit var btnHistory: MaterialButton

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_bookings)

        rvBookings = findViewById(R.id.rvBookings)
        btnBookNew = findViewById(R.id.btnBookNew)
        btnHistory = findViewById(R.id.btnHistory)

        val bookings = listOf(
            Booking("BK001", "Mike's Garage", "Confirmed", "2024-10-15", "10:00 AM", "123 Workshop Street, City", listOf("Oil Change", "Brake Check"), "₹650"),
            Booking("BK002", "Speedy Service", "Pending", "2024-10-20", "02:00 PM", "456 Service Road, City", listOf("Chain Cleaning", "Battery Check"), "₹250")
        )

        rvBookings.layoutManager = LinearLayoutManager(this)
        rvBookings.adapter = BookingAdapter(bookings)

        btnBookNew.setOnClickListener {
            startActivity(Intent(this, FindWorkshopActivity::class.java))
        }

        btnHistory.setOnClickListener {
            startActivity(Intent(this, ServiceHistoryActivity::class.java))
        }
        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_bookings



        setupBottomNav()
    }
    private fun setupBottomNav() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home clicked...", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_search -> {
                    Toast.makeText(this, "Bookings clicked", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, FindWorkshopActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_bookings -> true
                R.id.nav_profile -> {
                    Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MyProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }
}
