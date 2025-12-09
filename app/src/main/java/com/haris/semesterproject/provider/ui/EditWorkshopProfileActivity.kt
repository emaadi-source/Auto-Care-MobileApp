package com.haris.semesterproject.provider.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.haris.semesterproject.databinding.ActivityEditWorkshopProfileBinding
import com.haris.semesterproject.provider.data.ProfileResponse
import com.haris.semesterproject.network.RetrofitClient
import com.haris.semesterproject.provider.data.SimpleResponse
import com.haris.semesterproject.provider.data.WorkshopProfile
import com.haris.semesterproject.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class EditWorkshopProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditWorkshopProfileBinding

    private var image1Base64: String? = null
    private var image2Base64: String? = null
    private var activeImageSelection = 1

    // Image Picker (Same as before)
    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val bitmap = getBitmapFromUri(uri)
            val base64 = convertBitmapToBase64(bitmap)

            if (activeImageSelection == 1) {
                binding.ivEditImage1.setImageBitmap(bitmap)
                image1Base64 = base64
            } else {
                binding.ivEditImage2.setImageBitmap(bitmap)
                image2Base64 = base64
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditWorkshopProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. FETCH DATA FROM SERVER (Fix for Crash)
        loadCurrentData()

        setupClicks()
    }

    private fun loadCurrentData() {
        val providerId = SessionManager(this).fetchUserId()

        // Reuse the getWorkshopProfile API
        RetrofitClient.api.getWorkshopProfile(providerId).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful && response.body()?.data != null) {
                    prefillData(response.body()!!.data!!)
                }
            }
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Failed to load current details", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupClicks() {
        binding.btnCancel.setOnClickListener { finish() }

        binding.ivEditImage1.setOnClickListener {
            activeImageSelection = 1
            imagePicker.launch("image/*")
        }

        binding.ivEditImage2.setOnClickListener {
            activeImageSelection = 2
            imagePicker.launch("image/*")
        }

        binding.btnSaveChanges.setOnClickListener {
            saveChanges()
        }
    }

    private fun prefillData(data: WorkshopProfile) {
        binding.etWorkshopName.setText(data.workshop_name)
        binding.etOwnerName.setText(data.owner_name)
        binding.etContactEmail.setText(data.contact_email)
        binding.etContactPhone.setText(data.contact_phone)
        binding.etDescription.setText(data.description)
        binding.etAddress.setText(data.address)
        binding.etCity.setText(data.city)
        binding.etPincode.setText(data.pincode)

        // Decode existing images
        if (!data.image_1.isNullOrEmpty()) {
            image1Base64 = data.image_1
            val bytes = Base64.decode(data.image_1, Base64.DEFAULT)
            binding.ivEditImage1.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
        }
        if (!data.image_2.isNullOrEmpty()) {
            image2Base64 = data.image_2
            val bytes = Base64.decode(data.image_2, Base64.DEFAULT)
            binding.ivEditImage2.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
        }
    }

    private fun saveChanges() {
        val providerId = SessionManager(this).fetchUserId()
        val name = binding.etWorkshopName.text.toString()

        if (name.isEmpty()) {
            Toast.makeText(this, "Workshop Name is required", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.api.updateWorkshopProfile(
            providerId,
            name,
            binding.etOwnerName.text.toString(),
            binding.etDescription.text.toString(),
            binding.etAddress.text.toString(),
            binding.etCity.text.toString(),
            binding.etPincode.text.toString(),
            binding.etContactEmail.text.toString(),
            binding.etContactPhone.text.toString(),
            image1Base64,
            image2Base64
        ).enqueue(object : Callback<SimpleResponse> {
            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                if (response.isSuccessful && response.body()?.error == false) {
                    Toast.makeText(applicationContext, "Profile Updated!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Failed: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        // Compress heavily (quality 50) to make sure it uploads fast and doesn't crash
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        val bytes = stream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
}