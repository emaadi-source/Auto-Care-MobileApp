package com.haris.semesterproject.customer.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.haris.semesterproject.R
import com.haris.semesterproject.customer.adapter.ServiceAdapter
import com.haris.semesterproject.customer.data.BookingResponse
import com.haris.semesterproject.customer.data.BookingService
import com.haris.semesterproject.customer.data.ServiceWhole
import com.haris.semesterproject.network.RetrofitClient
import com.haris.semesterproject.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class BookingActivity : AppCompatActivity() {

    private lateinit var tvSelectedDate: TextView
    private lateinit var tvTotalPrice: TextView
    private lateinit var btnConfirm: MaterialButton
    private lateinit var rvServices: RecyclerView
    private lateinit var etVehicleModel: TextInputEditText
    private lateinit var etVehicleNumber: TextInputEditText

    private var providerId: Int = 0
    private var currentUserId: Int = 0
    private var selectedDateApi: String = ""
    private val selectedServices = HashMap<Int, Double>()
    private val servicesList = ArrayList<ServiceWhole>()

    private lateinit var adapter: ServiceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        // Setup Toolbar
        findViewById<MaterialToolbar>(R.id.topAppBarBooking).setNavigationOnClickListener { finish() }

        // Bind Views
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)
        btnConfirm = findViewById(R.id.btnConfirmBooking)
        rvServices = findViewById(R.id.rvServicesBooking)
        etVehicleModel = findViewById(R.id.etVehicleModel)
        etVehicleNumber = findViewById(R.id.etVehicleNumber)

        // Get IDs
        providerId = intent.getIntExtra("providerId", 0)
        currentUserId = SessionManager(this).fetchUserId()

        // Validation Check on Load
        if (providerId == 0 || currentUserId == -1 || currentUserId == 0) {
            Toast.makeText(this, "Error: Invalid User or Provider ID", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Setup Adapter
        adapter = ServiceAdapter(servicesList, selectedServices) { updateTotalPrice() }
        rvServices.layoutManager = LinearLayoutManager(this)
        rvServices.adapter = adapter

        // Listeners
        tvSelectedDate.setOnClickListener { openDatePicker() }
        btnConfirm.setOnClickListener { confirmBooking() }

        loadServices()
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            val cal = Calendar.getInstance().apply { set(year, month, day) }
            // This format is perfect for MySQL
            selectedDateApi = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)

            // This format is for the User to see
            val displayDate = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(cal.time)
            tvSelectedDate.text = "Date: $displayDate"
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun loadServices() {
        RetrofitClient.api.getServicesForCustomer(providerId).enqueue(object : Callback<List<ServiceWhole>> {
            override fun onResponse(call: Call<List<ServiceWhole>>, response: Response<List<ServiceWhole>>) {
                if (response.isSuccessful && response.body() != null) {
                    servicesList.clear()
                    servicesList.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@BookingActivity, "Failed to load services", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ServiceWhole>>, t: Throwable) {
                Toast.makeText(this@BookingActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateTotalPrice() {
        tvTotalPrice.text = "Total: Rs ${selectedServices.values.sum()}"
    }

    private fun confirmBooking() {
        val model = etVehicleModel.text.toString().trim()
        val number = etVehicleNumber.text.toString().trim()

        // 1. Validate Inputs
        if (selectedDateApi.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }
        if (model.isEmpty() || number.isEmpty()) {
            Toast.makeText(this, "Please enter vehicle details", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedServices.isEmpty()) {
            Toast.makeText(this, "Please select at least one service", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Prepare Data
        val serviceArray = selectedServices.map { BookingService(it.key, it.value) }
        val json = Gson().toJson(serviceArray)
        val totalPrice = selectedServices.values.sum()

        btnConfirm.isEnabled = false
        btnConfirm.text = "Processing..."

        // 3. API Call
        RetrofitClient.api.createBooking(
            customerId = currentUserId,
            providerId = providerId,
            date = selectedDateApi,
            totalAmount = totalPrice,
            servicesJson = json,
            vehicleModel = model,
            vehicleNumber = number
        ).enqueue(object : Callback<BookingResponse> {
            override fun onResponse(call: Call<BookingResponse>, response: Response<BookingResponse>) {
                btnConfirm.isEnabled = true
                btnConfirm.text = "Confirm Booking"

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Toast.makeText(this@BookingActivity, body.message, Toast.LENGTH_LONG).show()
                    if (body.success) finish()
                } else {
                    // --- IMPROVED ERROR DEBUGGING ---
                    try {
                        val errorBody = response.errorBody()?.string()
                        Log.e("BookingError", "Code: ${response.code()}, Error: $errorBody")
                        Toast.makeText(this@BookingActivity, "Server Error: $errorBody", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@BookingActivity, "Server Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                btnConfirm.isEnabled = true
                btnConfirm.text = "Confirm Booking"
                Log.e("BookingFailure", "Error: ${t.message}", t)
                Toast.makeText(this@BookingActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}