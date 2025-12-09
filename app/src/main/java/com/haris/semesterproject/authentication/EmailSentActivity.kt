package com.haris.semesterproject.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.haris.semesterproject.databinding.ActivityEmailSentBinding

class EmailSentActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityEmailSentBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        var back = binding.backArrow
        var backToSignIn = binding.backToSignin


        back.setOnClickListener {
            var intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }


        backToSignIn.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}