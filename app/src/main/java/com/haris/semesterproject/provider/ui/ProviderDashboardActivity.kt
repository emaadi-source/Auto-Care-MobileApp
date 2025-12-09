package com.haris.semesterproject.provider.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.haris.semesterproject.R
import com.haris.semesterproject.authentication.LoginActivity
import com.haris.semesterproject.databinding.ActivityProviderDashboardBinding
import com.haris.semesterproject.network.RetrofitClient
import com.haris.semesterproject.provider.adapter.BookingAdapter
import com.haris.semesterproject.provider.data.Booking
import com.haris.semesterproject.provider.data.DashboardStatsResponse
import com.haris.semesterproject.utils.ProviderNavigation
import com.haris.semesterproject.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProviderDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProviderDashboardBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var bookingAdapter: BookingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupUI()

        // Setup Bottom Navigation
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.providerBottomNav)
        ProviderNavigation.setup(this, bottomNav, R.id.navigation_home)
    }

    override fun onResume() {
        super.onResume()
        // Reload data every time we return to this screen (e.g., after accepting a booking)
        loadDashboardData()
    }

    private fun setupUI() {
        val userName = sessionManager.fetchUserName() ?: "Provider"
        binding.welcomeText.text = "Welcome back, $userName"

        binding.logoutIcon.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("logout", true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Setup List
        bookingAdapter = BookingAdapter(emptyList())
        binding.recyclerBookings.apply {
            layoutManager = LinearLayoutManager(this@ProviderDashboardActivity)
            adapter = bookingAdapter
        }

        // --- BUTTONS ---

        binding.btnManageServices.setOnClickListener {
            startActivity(Intent(this, ManageServicesActivity::class.java))
        }

        binding.btnWorkshopProfile.setOnClickListener {
            startActivity(Intent(this, WorkshopProfileActivity::class.java))
        }

        binding.btnViewBookings.setOnClickListener {
            startActivity(Intent(this, IncomingBookingsActivity::class.java))
        }

        // 4. View Earnings (Clicking the Stats Card)
        // Ensure you added android:id="@+id/cardEarnings" in XML as shown in Step 1
        try {
            binding.cardEarnings.setOnClickListener {
                startActivity(Intent(this, EarningsActivity::class.java))
            }
        } catch (e: Exception) {
            // Fallback if ID is missing or binding fails
            e.printStackTrace()
        }
    }
    
    private fun loadDashboardData() {
        val providerId = sessionManager.fetchUserId()
        if (providerId == -1) {
            Toast.makeText(this, "Error: Invalid Provider ID", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Get Stats
        RetrofitClient.api.getDashboardStats(providerId).enqueue(object : Callback<DashboardStatsResponse> {
            override fun onResponse(call: Call<DashboardStatsResponse>, response: Response<DashboardStatsResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val stats = response.body()!!
                    if (!stats.error) {
                        // Update UI
                        binding.tvTotalBookings.text = stats.total_bookings.toString()
                        binding.tvEarnings.text = "Rs ${stats.earnings}" // Formatted
                        binding.tvPendingCount.text = stats.pending_today.toString()
                        binding.tvCompletedCount.text = stats.completed_today.toString()

                        Log.d("Dashboard", "Stats Loaded: ${stats.earnings}")
                    }
                }
            }
            override fun onFailure(call: Call<DashboardStatsResponse>, t: Throwable) {
                Log.e("Dashboard", "Stats Failed: ${t.message}")
            }
        })

        // 2. Get Recent Bookings
        RetrofitClient.api.getProviderBookings(providerId).enqueue(object : Callback<List<Booking>> {
            override fun onResponse(call: Call<List<Booking>>, response: Response<List<Booking>>) {
                if (response.isSuccessful && response.body() != null) {
                    val bookings = response.body()!!
                    // Show only top 5 recent bookings
                    val recentBookings = bookings.take(5)
                    bookingAdapter.updateBookings(recentBookings)
                }
            }
            override fun onFailure(call: Call<List<Booking>>, t: Throwable) {
                Log.e("Dashboard", "Bookings Failed: ${t.message}")
            }
        })
    }
}