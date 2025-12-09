package com.haris.semesterproject.customer.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.haris.semesterproject.R
import com.haris.semesterproject.customer.adapter.WorkshopAdapter
import com.haris.semesterproject.customer.data.Workshop
import com.haris.semesterproject.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindWorkshopActivity : AppCompatActivity() {

    private lateinit var rvWorkshops: RecyclerView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var etSearch: EditText

    // Initialize adapter with empty list to prevent crash
    private var adapter: WorkshopAdapter = WorkshopAdapter(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_workshop)

        rvWorkshops = findViewById(R.id.rvWorkshops)
        bottomNav = findViewById(R.id.bottomNav)
        etSearch = findViewById(R.id.etSearchWorkshop)

        rvWorkshops.layoutManager = LinearLayoutManager(this)
        rvWorkshops.adapter = adapter

        setupSearch()
        setupBottomNav()
        loadWorkshops()
    }

    private fun loadWorkshops() {
        RetrofitClient.api.getAllWorkshops().enqueue(object : Callback<List<Workshop>> {
            override fun onResponse(
                call: Call<List<Workshop>>,
                response: Response<List<Workshop>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val workshopList = response.body()!!
                    adapter.updateData(workshopList)
                } else {
                    Toast.makeText(
                        this@FindWorkshopActivity,
                        "Error loading workshops",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Workshop>>, t: Throwable) {
                Toast.makeText(
                    this@FindWorkshopActivity,
                    "Network error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(query: CharSequence?, start: Int, before: Int, count: Int) {
                // Safe call to adapter to prevent crashes
                adapter.filter(query.toString())
            }
        })
    }

    private fun setupBottomNav() {
        bottomNav.selectedItemId = R.id.nav_search

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_search -> true
                R.id.nav_bookings -> {
                    startActivity(Intent(this, MyBookingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, MyProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }
}
