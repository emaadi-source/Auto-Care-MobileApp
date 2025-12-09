package com.haris.semesterproject.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.haris.semesterproject.databinding.ActivityVerificationBinding

class VerificationActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityVerificationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        var verifyBtn = binding.verifyButton
        var back = binding.backArrow

        verifyBtn.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        back.setOnClickListener {
            var intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}