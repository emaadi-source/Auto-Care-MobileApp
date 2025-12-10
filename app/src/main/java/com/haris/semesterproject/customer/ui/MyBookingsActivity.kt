package com.haris.semesterproject.customer.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.customer.adapter.BookingAdapter
import com.haris.semesterproject.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.haris.semesterproject.customer.adapter.BookingAdapter1
import com.haris.semesterproject.customer.data.BookingResponseNew
import com.haris.semesterproject.customer.data.NewBooking
import com.haris.semesterproject.customer.helper.BookingDBHelper
import com.haris.semesterproject.network.RetrofitClient
import com.haris.semesterproject.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



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


        rvBookings.layoutManager = LinearLayoutManager(this)

        btnBookNew.setOnClickListener {
            startActivity(Intent(this, FindWorkshopActivity::class.java))
        }

        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_bookings

        loadCustomerBookings()

        setupBottomNav()
    }
    private fun loadCustomerBookings() {
        val session = SessionManager(this)
        val customerId = session.fetchUserId().toInt()
        val dbHelper = BookingDBHelper(this)

        if (isOnline()) {
            // Online mode: fetch from API
            RetrofitClient.api.getCustomerBookings(customerId)
                .enqueue(object : Callback<BookingResponseNew> {
                    override fun onResponse(
                        call: Call<BookingResponseNew>,
                        response: Response<BookingResponseNew>
                    ) {
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
                                        address = "${item.vehicleModel ?: ""} ${item.vehicleNumber ?: ""}",
                                        services = emptyList(),
                                        price = item.totalPrice
                                    )
                                )
                            }

                            // Display online bookings
                            rvBookings.adapter = BookingAdapter(bookingList)

                            // Save online bookings to SQLite for offline use
                            Thread {
                                dbHelper.insertBookings(bookingList)
                            }.start()

                        } else {
                            // API returned no bookings → fallback to offline
                            loadBookingsOffline(dbHelper)
                        }
                    }

                    override fun onFailure(call: Call<BookingResponseNew>, t: Throwable) {
                        // API failed → fallback to offline
                        loadBookingsOffline(dbHelper)
                    }
                })
        } else {
            // Offline mode: load from SQLite
            loadBookingsOffline(dbHelper)
        }
    }

    // Load bookings from SQLite (offline)
    private fun loadBookingsOffline(dbHelper: BookingDBHelper) {
        Thread {
            val offlineBookings = dbHelper.getAllBookings()
            runOnUiThread {
                if (offlineBookings.isNotEmpty()) {
                    rvBookings.adapter = BookingAdapter(offlineBookings.toMutableList())
                    Toast.makeText(this, "Loaded offline bookings", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No bookings available offline", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    // Utility function to check network
    private fun isOnline(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
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
