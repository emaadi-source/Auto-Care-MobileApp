package com.haris.semesterproject.provider.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.haris.semesterproject.R
import com.haris.semesterproject.databinding.ActivityBookingDetailBinding
import com.haris.semesterproject.network.RetrofitClient
import com.haris.semesterproject.provider.data.BookingDetails
import com.haris.semesterproject.provider.data.SimpleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingDetailBinding
    private var booking: BookingDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Get Data from Intent
        booking = intent.getSerializableExtra("booking_data") as? BookingDetails

        if (booking != null) {
            setupUI(booking!!)
        } else {
            Toast.makeText(this, "Error loading details", Toast.LENGTH_SHORT).show()
            finish()
        }

        // 2. Setup Buttons
        binding.btnBack.setOnClickListener { finish() }

        binding.btnCallCustomer.setOnClickListener {
            val intent = Intent(this, ProviderChatActivity::class.java) // OR OutgoingCallActivity
            intent.putExtra("customer_id", booking?.customer_id) // Ensure this field exists in BookingDetails
            intent.putExtra("customer_name", booking?.customer_name)
            startActivity(intent)
        }

        // 3. Status Action Buttons
        binding.btnAccept.setOnClickListener { updateStatus("confirmed") }
        binding.btnDecline.setOnClickListener { updateStatus("cancelled") }
        binding.btnComplete.setOnClickListener { updateStatus("completed") }
    }

    private fun setupUI(data: BookingDetails) {
        binding.tvHeaderSubtitle.text = "#${data.id} • ${data.customer_name}"
        binding.tvStatus.text = data.status.capitalize()

        // --- LOGIC TO SHOW/HIDE BUTTONS BASED ON STATUS ---
        when (data.status.lowercase()) {
            "pending" -> {
                binding.ivStatusDot.setColorFilter(ContextCompat.getColor(this, R.color.status_pending_orange))

                binding.bottomActions.visibility = View.VISIBLE
                binding.btnAccept.visibility = View.VISIBLE
                binding.btnDecline.visibility = View.VISIBLE
                binding.btnComplete.visibility = View.GONE
            }
            "confirmed" -> {
                binding.ivStatusDot.setColorFilter(ContextCompat.getColor(this, R.color.status_green_text))

                binding.bottomActions.visibility = View.VISIBLE
                binding.btnAccept.visibility = View.GONE
                binding.btnDecline.visibility = View.GONE
                binding.btnComplete.visibility = View.VISIBLE
            }
            else -> {
                // Completed or Cancelled
                binding.ivStatusDot.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
                binding.bottomActions.visibility = View.GONE
            }
        }

        // Customer Info
        binding.tvCustomerName.text = data.customer_name
        binding.tvCustomerPhone.text = data.customer_phone
        binding.tvCustomerEmail.text = data.customer_email

        // Service Info
        binding.tvServiceName.text = data.main_service ?: "General Service"
        binding.tvTotal.text = "Rs ${data.total}"

        // Vehicle Info
        binding.tvVehicleModel.text = data.vehicle_model ?: "N/A"
        binding.tvVehicleNumber.text = data.vehicle_number ?: "N/A"
        binding.tvProblemDesc.text = data.problem_description ?: "No description provided"
    }

    private fun updateStatus(newStatus: String) {
        booking?.let { b ->
            RetrofitClient.api.updateBookingStatus(b.id, newStatus).enqueue(object : Callback<SimpleResponse> {
                override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                    if (response.isSuccessful && response.body()?.error == false) {
                        Toast.makeText(applicationContext, "Booking updated to $newStatus", Toast.LENGTH_SHORT).show()

                        // Update local object to reflect change immediately without reloading activity
                        b.status = newStatus
                        setupUI(b)

                        // Optionally finish() to go back to list
                        // finish()
                    } else {
                        Toast.makeText(applicationContext, "Failed to update", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Network Error", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}