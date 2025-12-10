package com.haris.semesterproject.customer.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.haris.semesterproject.R
import com.haris.semesterproject.network.Api
import com.haris.semesterproject.network.RetrofitClient
import com.haris.semesterproject.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        sessionManager = SessionManager(this)

        val edtFullName = findViewById<TextInputEditText>(R.id.editUsername)
        val edtEmail = findViewById<TextInputEditText>(R.id.editEmail)
        val edtPassword = findViewById<TextInputEditText>(R.id.editPassword)
        val edtConfirmPassword = findViewById<TextInputEditText>(R.id.cnfrmPassword)
        val btnSave = findViewById<MaterialButton>(R.id.btnUpdateProfile)

        // Load current user info
        edtFullName.setText(sessionManager.fetchUserName())
        edtEmail.setText(sessionManager.fetchUserEmail())

        btnSave.setOnClickListener {
            val fullName = edtFullName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val confirmPassword = edtConfirmPassword.text.toString().trim()

            // Basic validations
            if (fullName.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Name and Email cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isNotEmpty() && password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = sessionManager.fetchUserId()

            // Call API to update
            RetrofitClient.api.updateUserProfile(userId, fullName, email, password.ifEmpty { null })
                .enqueue(object : Callback<Api.UpdateProfileResponse> {
                    override fun onResponse(
                        call: Call<Api.UpdateProfileResponse>,
                        response: Response<Api.UpdateProfileResponse>
                    ) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(this@EditProfileActivity, "Profile updated!", Toast.LENGTH_SHORT).show()
                            sessionManager.saveUser(userId, fullName, email)
                            finish()
                        } else {
                            Toast.makeText(this@EditProfileActivity, "Update failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Api.UpdateProfileResponse>, t: Throwable) {
                        Toast.makeText(this@EditProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
