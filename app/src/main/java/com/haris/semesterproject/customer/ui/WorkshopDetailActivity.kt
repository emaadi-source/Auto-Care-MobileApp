package com.haris.semesterproject.customer.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.haris.semesterproject.R
import com.haris.semesterproject.customer.data.Service
import com.haris.semesterproject.network.Api
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.haris.semesterproject.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorkshopDetailActivity : AppCompatActivity() {

    private lateinit var layoutServices: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_detail)

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBarDetail)
        toolbar.setNavigationOnClickListener { finish() }

        // Views
        val imgWorkshop = findViewById<ImageView>(R.id.imgWorkshopDetail)
        val tvName = findViewById<TextView>(R.id.tvWorkshopNameDetail)
        val tvDesc = findViewById<TextView>(R.id.tvWorkshopDesc)
        layoutServices = findViewById(R.id.layoutServices) // Add this ID in XML for dynamic chips container

        // Data from Intent
        val name = intent.getStringExtra("WORKSHOP_NAME") ?: ""
        val details = intent.getStringExtra("WORKSHOP_DETAILS") ?: ""
        val imageBase64 = intent.getStringExtra("WORKSHOP_IMAGE") ?: ""
        val providerId = intent.getIntExtra("WORKSHOP_PROVIDER_ID", 0)


        tvName.text = name
        tvDesc.text = details

        // Load image
        try {
            val imgBytes = Base64.decode(imageBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.size)
            imgWorkshop.setImageBitmap(bitmap)
        } catch (e: Exception) {
            imgWorkshop.setImageResource(R.drawable.sample_bike)
        }

        // Buttons
        findViewById<MaterialButton>(R.id.btnCallWorkshop).setOnClickListener {
            val intent = Intent(this, BookingActivity::class.java)
            intent.putExtra("providerId", providerId.toInt())
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btnMessageWorkshop).setOnClickListener {
            val intent = Intent(this, chat_paage::class.java)
            // Pass the receiver ID or provider ID to the chat page
            intent.putExtra("providerId", providerId.toInt())
            // If needed, pass the current user ID as well
            // intent.putExtra("currentUserId", currentUserId)
            startActivity(intent)
        }


        findViewById<MaterialButton>(R.id.btnRateWorkshop).setOnClickListener {
            val intent = Intent(this, WorkshopRatingActivity::class.java)
            startActivity(intent)
        }

        // Load services dynamically
        loadServices(providerId)
    }

    private fun loadServices(providerId: Int) {
        val api = RetrofitClient.retrofit.create(Api::class.java)
        api.getServices(providerId).enqueue(object : Callback<List<Service>> {
            override fun onResponse(call: Call<List<Service>>, response: Response<List<Service>>) {
                if (response.isSuccessful) {
                    val services = response.body() ?: emptyList()
                    showServiceChips(services)
                }
            }

            override fun onFailure(call: Call<List<Service>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun showServiceChips(services: List<Service>) {
        layoutServices.removeAllViews()

        for (service in services) {
            val chip = TextView(this)
            chip.text = service.name
            chip.setPadding(20, 10, 20, 10)
            chip.setBackgroundResource(R.drawable.bg_chip)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(10, 0, 10, 0)
            chip.layoutParams = params

            layoutServices.addView(chip)
        }
    }
}
