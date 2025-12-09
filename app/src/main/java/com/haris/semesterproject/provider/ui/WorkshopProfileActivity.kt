package com.haris.semesterproject.provider.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.haris.semesterproject.R
import com.haris.semesterproject.databinding.ActivityWorkshopProfileBinding
import com.haris.semesterproject.provider.data.ProfileResponse
import com.haris.semesterproject.network.RetrofitClient
import com.haris.semesterproject.provider.data.WorkshopProfile
import com.haris.semesterproject.utils.ProviderNavigation
import com.haris.semesterproject.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorkshopProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkshopProfileBinding
    private var currentProfile: WorkshopProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkshopProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClicks()

        // Inside onCreate
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.providerBottomNav)
        ProviderNavigation.setup(this, bottomNav, R.id.navigation_profile)
    }

    override fun onResume() {
        super.onResume()
        loadProfileData() // Reload data when returning from Edit Screen
    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener { finish() }

        // Edit Icon -> Just open the activity (Don't pass the heavy data)
        binding.ivEdit.setOnClickListener {
            val intent = Intent(this, EditWorkshopProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnManageServices.setOnClickListener {
            startActivity(Intent(this, ManageServicesActivity::class.java))
        }

        binding.btnViewReviews.setOnClickListener {
            Toast.makeText(this, "Reviews coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfileData() {
        val providerId = SessionManager(this).fetchUserId()

        RetrofitClient.api.getWorkshopProfile(providerId).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val resp = response.body()!!
                    if (!resp.error && resp.data != null) {
                        currentProfile = resp.data
                        updateUI(resp.data)
                    } else {
                        binding.tvWorkshopName.text = "No Profile Details Found"
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(data: WorkshopProfile) {
        binding.tvWorkshopName.text = data.workshop_name ?: "N/A"
        binding.tvOwnerName.text = data.owner_name ?: "N/A"
        binding.tvEmail.text = data.contact_email ?: "N/A"
        binding.tvPhone.text = data.contact_phone ?: "N/A"
        binding.tvDescription.text = data.description ?: "N/A"
        binding.tvAddress.text = data.address ?: "N/A"
        binding.tvCityPin.text = "${data.city ?: ""}, ${data.pincode ?: ""}"

        // Decode and Display Image 1
        if (!data.image_1.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(data.image_1, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                binding.ivDisplayImage1.setImageBitmap(decodedImage)
            } catch (e: Exception) { e.printStackTrace() }
        }

        // Decode and Display Image 2
        if (!data.image_2.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(data.image_2, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                binding.ivDisplayImage2.setImageBitmap(decodedImage)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
}