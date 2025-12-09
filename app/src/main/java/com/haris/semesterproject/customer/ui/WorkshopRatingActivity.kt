package com.haris.semesterproject.customer.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.haris.semesterproject.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class WorkshopRatingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_rating)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBarRating)
        toolbar.setNavigationOnClickListener { finish() }

        val ratingBar = findViewById<RatingBar>(R.id.ratingBarWorkshop)
        val etComment = findViewById<EditText>(R.id.etReviewComment)
        val btnSubmit = findViewById<MaterialButton>(R.id.btnSubmitRating)

        btnSubmit.setOnClickListener {
            val rating = ratingBar.rating
            val comment = etComment.text.toString().trim()

            if (rating == 0f) {
                Toast.makeText(this, "Please select a star rating", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // You can send this data to your server or database here
            Toast.makeText(
                this,
                "Thank you for rating $rating stars!\nComment: $comment",
                Toast.LENGTH_LONG
            ).show()

            // Close the rating page
            finish()
        }
    }
}
