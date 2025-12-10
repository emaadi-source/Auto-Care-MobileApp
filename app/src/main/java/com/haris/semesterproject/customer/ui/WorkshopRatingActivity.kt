package com.haris.semesterproject.customer.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.haris.semesterproject.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.haris.semesterproject.network.Api
import com.haris.semesterproject.network.RetrofitClient
import com.haris.semesterproject.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorkshopRatingActivity : AppCompatActivity() {

    private var workshopId: Int = 0
    private var customerId: Int = 0

    private var workshopName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_rating)

        val ratingBar = findViewById<RatingBar>(R.id.ratingBarWorkshop)
        val etComment = findViewById<EditText>(R.id.etReviewComment)
        val btnSubmit = findViewById<MaterialButton>(R.id.btnSubmitRating)
        val tvWorkshopName = findViewById<TextView>(R.id.tvWorkshopNameRating)

        // Get workshop ID from Intent
        workshopId = intent.getIntExtra("WORKSHOP_ID", 0)
        workshopName=intent.getStringExtra("WORKSHOP_NAME")

        // Get customer ID from session
        val session = SessionManager(this)
        customerId = session.fetchUserId()
        tvWorkshopName.text = workshopName



        btnSubmit.setOnClickListener {
            val rating = ratingBar.rating.toInt()
            val comment = etComment.text.toString().trim()

            if (rating == 0) {
                Toast.makeText(this, "Please select a star rating", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetrofitClient.api.submitWorkshopRating(customerId, workshopId, rating, comment)
                .enqueue(object : Callback<Api.SubmitRatingResponse> {
                    override fun onResponse(call: Call<Api.SubmitRatingResponse>, response: Response<Api.SubmitRatingResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(this@WorkshopRatingActivity, "Rating submitted!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@WorkshopRatingActivity, "Failed to submit rating", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Api.SubmitRatingResponse>, t: Throwable) {
                        Toast.makeText(this@WorkshopRatingActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

}
