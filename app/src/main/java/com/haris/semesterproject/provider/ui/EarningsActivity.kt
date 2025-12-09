package com.haris.semesterproject.provider.ui

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.haris.semesterproject.R
import com.haris.semesterproject.network.RetrofitClient
import com.haris.semesterproject.provider.adapter.EarningsAdapter
import com.haris.semesterproject.provider.data.EarningsResponse
import com.haris.semesterproject.utils.ProviderNavigation
import com.haris.semesterproject.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EarningsActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: EarningsAdapter

    // Views
    private lateinit var tvTotalBalance: TextView
    private lateinit var tvMonthly: TextView
    private lateinit var rvHistory: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_earnings)

        sessionManager = SessionManager(this)

        // Setup Toolbar
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        // Bind Views
        tvTotalBalance = findViewById(R.id.tvTotalBalance)
        tvMonthly = findViewById(R.id.tvMonthly)
        rvHistory = findViewById(R.id.rvHistory)

        // Setup RecyclerView
        adapter = EarningsAdapter(emptyList())
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = adapter

        loadEarnings()

        // Inside onCreate
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.providerBottomNav)
        ProviderNavigation.setup(this, bottomNav, R.id.navigation_earnings)
    }

    private fun loadEarnings() {
        val providerId = sessionManager.fetchUserId()

        RetrofitClient.api.getEarningsHistory(providerId).enqueue(object : Callback<EarningsResponse> {
            override fun onResponse(call: Call<EarningsResponse>, response: Response<EarningsResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!

                    // Update Texts
                    tvTotalBalance.text = "Rs ${data.total_earnings}"
                    tvMonthly.text = "Rs ${data.monthly_earnings}"

                    // Update List
                    adapter.updateData(data.history)
                }
            }

            override fun onFailure(call: Call<EarningsResponse>, t: Throwable) {
                Toast.makeText(this@EarningsActivity, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}