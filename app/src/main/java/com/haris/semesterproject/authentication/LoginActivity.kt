package com.haris.semesterproject.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.haris.semesterproject.databinding.ActivityLoginBinding
import com.haris.semesterproject.authentication.data.LoginResponse
import com.haris.semesterproject.customer.ui.MainActivity
import com.haris.semesterproject.network.RetrofitClient
import com.haris.semesterproject.provider.ui.ProviderDashboardActivity
// import com.haris.semesterproject.customer.ui.CustomerDashboardActivity
import com.haris.semesterproject.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        val cameFromLogout = intent.getBooleanExtra("logout", false)

        if (!cameFromLogout && sessionManager.fetchUserId() != -1) {
            navigateBasedOnRole(sessionManager.fetchUserRole())
        }

        val intent = Intent(this, IncomingCallService::class.java)
        startForegroundService(intent)
        startService(intent)



        binding.signupLink.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.api.login(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!

                    if (!loginResponse.error) {
                        // FIX STARTS HERE
                        val user = loginResponse.user

                        // Check if user is not null before accessing properties
                        if (user != null) {
                            Toast.makeText(applicationContext, "Login Successful!", Toast.LENGTH_SHORT).show()
                            sessionManager.saveUser(user.id, user.full_name, user.email,user.role)
                            navigateBasedOnRole(user.role)
                        } else {
                            Toast.makeText(applicationContext, "Error: User data is missing", Toast.LENGTH_SHORT).show()
                        }
                        // FIX ENDS HERE

                    } else {
                        Toast.makeText(applicationContext, loginResponse.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Server Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("LoginError", t.message.toString())
            }
        })
    }

    private fun navigateBasedOnRole(role: String?) {
        val intent: Intent
        if (role == "provider") {
            intent = Intent(this, ProviderDashboardActivity::class.java)
        } else {

            intent = Intent(this, MainActivity::class.java)
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}