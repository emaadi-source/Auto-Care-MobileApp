package com.haris.semesterproject.provider.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.haris.semesterproject.R
import com.haris.semesterproject.databinding.ActivityIncomingBookingsBinding
import com.haris.semesterproject.network.RetrofitClient
import com.haris.semesterproject.provider.adapter.IncomingBookingAdapter
import com.haris.semesterproject.provider.data.BookingDetails
import com.haris.semesterproject.provider.data.SimpleResponse
import com.haris.semesterproject.utils.ProviderNavigation
import com.haris.semesterproject.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IncomingBookingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIncomingBookingsBinding
    private lateinit var adapter: IncomingBookingAdapter
    private lateinit var sessionManager: SessionManager
    private var currentFilter = "pending"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomingBookingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupRecyclerView()
        setupTabs()
        loadBookings()

        // Inside onCreate
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.providerBottomNav)
        ProviderNavigation.setup(this, bottomNav, R.id.navigation_bookings)
    }

    private fun setupRecyclerView() {
        adapter = IncomingBookingAdapter(emptyList(),
            onAccept = { booking -> updateStatus(booking.id, "confirmed") },
            onDecline = { booking -> updateStatus(booking.id, "cancelled") },
            onClick = { booking ->
                // Navigate to Detail Page (You need to create this Activity based on Screenshot 37)
                val intent = Intent(this, BookingDetailActivity::class.java)
                intent.putExtra("booking_data", booking)
                startActivity(intent)
            }
        )
        binding.recyclerBookings.layoutManager = LinearLayoutManager(this)
        binding.recyclerBookings.adapter = adapter
    }

    private fun setupTabs() {
        binding.btnTabPending.setOnClickListener {
            currentFilter = "pending"
            loadBookings()
            // Toggle Button Colors (Visual logic omitted for brevity)
        }
        binding.btnTabConfirmed.setOnClickListener {
            currentFilter = "confirmed"
            loadBookings()
        }
    }

    private fun loadBookings() {
        val providerId = sessionManager.fetchUserId()
        RetrofitClient.api.getIncomingBookings(providerId, currentFilter).enqueue(object : Callback<List<BookingDetails>> {
            override fun onResponse(call: Call<List<BookingDetails>>, response: Response<List<BookingDetails>>) {
                if (response.isSuccessful && response.body() != null) {
                    adapter.updateList(response.body()!!)
                }
            }
            override fun onFailure(call: Call<List<BookingDetails>>, t: Throwable) {
                Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateStatus(bookingId: Int, status: String) {
        RetrofitClient.api.updateBookingStatus(bookingId, status).enqueue(object : Callback<SimpleResponse> {
            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                if (response.isSuccessful && response.body()?.error == false) {
                    Toast.makeText(applicationContext, "Booking $status", Toast.LENGTH_SHORT).show()
                    loadBookings() // Refresh list
                }
            }
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}