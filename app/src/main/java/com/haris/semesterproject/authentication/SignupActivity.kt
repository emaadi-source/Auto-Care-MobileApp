package com.haris.semesterproject.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.haris.semesterproject.databinding.ActivitySignupBinding
import com.haris.semesterproject.authentication.data.LoginResponse
import com.haris.semesterproject.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Navigate back to Login
        binding.signInLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Handle Create Account Button
        binding.createAccountBtn.setOnClickListener {
            performSignup()
        }

        // Removed listeners for Google/Facebook as requested
    }

    private fun performSignup() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etSignupEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etSignupPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // 1. Get Selected Role (Customer or Provider)
        // Default is customer if nothing selected, or check ID
        val selectedRoleId = binding.joinAsGroup.checkedRadioButtonId
        val role = if (selectedRoleId == binding.providerRadio.id) "provider" else "customer"

        // 2. Validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (!binding.termsCheckbox.isChecked) {
            Toast.makeText(this, "Please accept Terms and Conditions", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. API Call
        RetrofitClient.api.signup(fullName, email, password, phone, role)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val loginResponse = response.body()!!

                        if (!loginResponse.error) {
                            Toast.makeText(applicationContext, "Registration Successful!", Toast.LENGTH_LONG).show()
                            // Go to Login Page
                            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(applicationContext, loginResponse.message, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "Server Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("SignupError", t.message.toString())
                }
            })
    }
}