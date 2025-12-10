package com.haris.semesterproject.customer.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.haris.semesterproject.authentication.LoginActivity
import com.haris.semesterproject.customer.adapter.BookingAdapter
import com.haris.semesterproject.customer.adapter.BookingAdapter1
import com.haris.semesterproject.customer.data.BookingResponseNew
import com.haris.semesterproject.customer.data.NewBooking
import com.haris.semesterproject.customer.helper.BookingDBHelper
import com.haris.semesterproject.network.RetrofitClient
import com.haris.semesterproject.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var rvCurrentStatus: RecyclerView
    private lateinit var rvRecentBookings: RecyclerView
    private lateinit var bottomNav: BottomNavigationView

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvRecentBookings = findViewById(R.id.rvRecentBookings)
        bottomNav = findViewById(R.id.bottomNav)
        var logbtn= findViewById<TextView>(R.id.tvWelcome)
        bottomNav.selectedItemId = R.id.nav_home
        sessionManager = SessionManager(this)
        logbtn.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("logout", true)   // 🚀 IMPORTANT
            startActivity(intent)
            finish()
        }

        rvRecentBookings = findViewById(R.id.rvRecentBookings)
        rvRecentBookings.setHasFixedSize(false)
        rvRecentBookings.layoutManager = LinearLayoutManager(this)


        loadCustomerBookings()  // Load bookings from API
        setupBottomNav()

        updateGridIcons()
        setupGridClickListeners()
    }

    private fun loadCustomerBookings() {
        val session = SessionManager(this)
        val customerId = session.fetchUserId().toInt()

        RetrofitClient.api.getCustomerBookings(customerId)
            .enqueue(object : Callback<BookingResponseNew> {
                override fun onResponse(call: Call<BookingResponseNew>, response: Response<BookingResponseNew>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val items = response.body()!!.bookings
                        val bookingList = mutableListOf<NewBooking>()

                        for (item in items) {
                            bookingList.add(
                                NewBooking(
                                    bookingId = item.bookingId,
                                    workshopName = item.workshopName ?: "Unknown Workshop",
                                    status = item.status,
                                    date = item.bookingDate,
                                    time = item.bookingTime ?: "",
                                    address = item.address ?: "",  // <-- use address from API
                                    city = item.city ?: "",        // <-- pass city from API
                                    services = emptyList(),
                                    price = item.totalPrice
                                )
                            )
                        }


                        // Show in RecyclerView
                        rvRecentBookings.adapter = BookingAdapter1(bookingList)
                        rvRecentBookings.setExpanded()
                        loadBookingsOffline()
                        // Save to SQLite for offline
                        Thread {
                            val dbHelper = BookingDBHelper(this@MainActivity)
                            dbHelper.insertBookings(bookingList)
                        }.start()

                    } else {
                        loadBookingsOffline()
                    }
                }

                override fun onFailure(call: Call<BookingResponseNew>, t: Throwable) {
                    loadBookingsOffline()
                }
            })
    }

    private fun loadBookingsOffline() {
        Thread {
            val dbHelper = BookingDBHelper(this@MainActivity)
            val offlineBookings = dbHelper.getAllBookings()
            runOnUiThread {
                if (offlineBookings.isNotEmpty()) {
                    rvRecentBookings.adapter = BookingAdapter1(offlineBookings.toMutableList())
                  //  Toast.makeText(this, "Loaded offline bookings", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No offline bookings available", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
    fun RecyclerView.setExpanded() {
        val adapter = this.adapter
        if (adapter != null) {
            val params = this.layoutParams
            params.height = adapter.itemCount * 1000 // estimate item height
            this.layoutParams = params
        }
    }



    private fun setupBottomNav() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_search -> {
                    Toast.makeText(this, "Searching Workshops...", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, FindWorkshopActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_bookings -> {
                    Toast.makeText(this, "Bookings clicked", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MyBookingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
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

    private fun updateGridIcons() {
        val btnFindWorkshop = findViewById<LinearLayout>(R.id.btnFindWorkshop)
        val btnMyBookings = findViewById<LinearLayout>(R.id.btnMyBookings)
        val btnServiceHistory = findViewById<LinearLayout>(R.id.btnServiceHistory)
        val btnRateService = findViewById<LinearLayout>(R.id.btnSettings)

        // Set icons and text programmatically
        btnFindWorkshop.findViewById<ImageView>(R.id.ivIcon)
            .setImageResource(R.drawable.ic_find_workshop)
        btnFindWorkshop.findViewById<TextView>(R.id.tvTitle).text = "Find Workshop"

        btnMyBookings.findViewById<ImageView>(R.id.ivIcon)
            .setImageResource(R.drawable.ic_my_bookings)
        btnMyBookings.findViewById<TextView>(R.id.tvTitle).text = "My Bookings"

        btnServiceHistory.findViewById<ImageView>(R.id.ivIcon)
            .setImageResource(R.drawable.ic_service_wrench)
        btnServiceHistory.findViewById<TextView>(R.id.tvTitle).text = "Terms & Conditions"

        btnRateService.findViewById<ImageView>(R.id.ivIcon)
            .setImageResource(R.drawable.ic_settings)
        btnRateService.findViewById<TextView>(R.id.tvTitle).text = "Settings"
    }

    private fun setupGridClickListeners() {
        // Find each included grid item by ID
        val btnFindWorkshop = findViewById<LinearLayout>(R.id.btnFindWorkshop)
        val btnMyBookings = findViewById<LinearLayout>(R.id.btnMyBookings)
        val btnServiceHistory = findViewById<LinearLayout>(R.id.btnServiceHistory)
        val btnSettings = findViewById<LinearLayout>(R.id.btnSettings)

        // Set click listeners to open corresponding activities
        btnFindWorkshop.setOnClickListener {
            startActivity(Intent(this, FindWorkshopActivity::class.java))
        }

        btnMyBookings.setOnClickListener {
            startActivity(Intent(this, MyBookingsActivity::class.java))
        }

        btnServiceHistory.setOnClickListener {
            startActivity(Intent(this, MyProfileActivity::class.java))
        }



        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java)) // Make sure this activity exists
        }
    }

}
