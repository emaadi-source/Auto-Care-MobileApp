package com.haris.semesterproject.provider.ui

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.haris.semesterproject.R
import com.haris.semesterproject.network.RetrofitClient
import com.haris.semesterproject.provider.adapter.ServicePartAdapter
import com.haris.semesterproject.provider.data.ItemType
import com.haris.semesterproject.provider.data.ServiceListResponse
import com.haris.semesterproject.provider.data.SimpleResponse
import com.haris.semesterproject.provider.data.ServiceItem
import com.haris.semesterproject.utils.ProviderNavigation
import com.haris.semesterproject.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageServicesActivity : AppCompatActivity(), AddItemBottomSheet.OnItemAddedListener {

    private lateinit var adapter: ServicePartAdapter
    private val allItems = mutableListOf<ServiceItem>()
    private var currentType = ItemType.SERVICE

    private lateinit var btnServices: Button
    private lateinit var btnParts: Button
    private lateinit var btnAddNew: Button
    private lateinit var rvList: RecyclerView
    private lateinit var btnBack: ImageView
    private lateinit var etSearch: EditText
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_services)

        sessionManager = SessionManager(this)
        setupViews()

        // Initialize Adapter with Callbacks
        adapter = ServicePartAdapter(
            mutableListOf(),
            onDeleteClick = { item -> confirmDelete(item) },
            onToggleClick = { item, isActive -> toggleItemStatus(item, isActive) }
        )

        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = adapter

        loadDataFromServer()

        // Tab Listeners
        btnServices.setOnClickListener { switchTab(ItemType.SERVICE) }
        btnParts.setOnClickListener { switchTab(ItemType.PART) }
        btnBack.setOnClickListener { finish() }

        btnAddNew.setOnClickListener {
            val bottomSheet = AddItemBottomSheet.newInstance(currentType)
            bottomSheet.show(supportFragmentManager, "AddItemSheet")
        }

        // Search Listener
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
        })

        // Navigation
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.providerBottomNav)
        ProviderNavigation.setup(this, bottomNav, R.id.navigation_services)
    }

    private fun setupViews() {
        btnServices = findViewById(R.id.btnTabServices)
        btnParts = findViewById(R.id.btnTabParts)
        btnAddNew = findViewById(R.id.btnAddNew)
        rvList = findViewById(R.id.recyclerView)
        btnBack = findViewById(R.id.btnBack)

        // Ensure you added android:id="@+id/etSearch" to the EditText in your XML
        etSearch = findViewById(R.id.etSearch)

        highlightTab(btnServices, btnParts)
        btnAddNew.text = "+ Add New Service"
    }

    private fun loadDataFromServer() {
        val providerId = sessionManager.fetchUserId()
        RetrofitClient.api.getProviderServices(providerId).enqueue(object : Callback<ServiceListResponse> {
            override fun onResponse(call: Call<ServiceListResponse>, response: Response<ServiceListResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (!apiResponse.error) {
                        allItems.clear()
                        allItems.addAll(apiResponse.data)
                        switchTab(currentType)
                    }
                }
            }
            override fun onFailure(call: Call<ServiceListResponse>, t: Throwable) {
                Toast.makeText(this@ManageServicesActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun confirmDelete(item: ServiceItem) {
        AlertDialog.Builder(this)
            .setTitle("Delete Item")
            .setMessage("Delete '${item.name}'?")
            .setPositiveButton("Delete") { _, _ -> deleteItemAPI(item) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteItemAPI(item: ServiceItem) {
        RetrofitClient.api.deleteService(item.id).enqueue(object : Callback<SimpleResponse> {
            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                if (response.isSuccessful && response.body()?.error == false) {
                    loadDataFromServer()
                    Toast.makeText(this@ManageServicesActivity, "Deleted", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {}
        })
    }

    private fun toggleItemStatus(item: ServiceItem, isActive: Boolean) {
        val statusInt = if (isActive) 1 else 0
        RetrofitClient.api.toggleService(item.id, statusInt).enqueue(object : Callback<SimpleResponse> {
            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                if (!response.isSuccessful) Toast.makeText(applicationContext, "Status update failed", Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {}
        })
    }

    private fun switchTab(type: ItemType) {
        currentType = type
        if (type == ItemType.SERVICE) {
            highlightTab(btnServices, btnParts)
            btnAddNew.text = "+ Add New Service"
        } else {
            highlightTab(btnParts, btnServices)
            btnAddNew.text = "+ Add New Spare Part"
        }
        val filteredList = allItems.filter { it.type == type }
        adapter.setData(filteredList)
    }

    private fun highlightTab(selected: Button, unselected: Button) {
        val maroon = ContextCompat.getColor(this, R.color.primary_maroon)
        selected.backgroundTintList = ColorStateList.valueOf(maroon)
        selected.setTextColor(Color.WHITE)
        unselected.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        unselected.setTextColor(Color.BLACK)
    }

    override fun onItemAdded(item: ServiceItem) {
        val providerId = sessionManager.fetchUserId()
        val typeString = if (item.type == ItemType.SERVICE) "SERVICE" else "PART"

        RetrofitClient.api.addServiceItem(
            providerId, item.name, item.category, item.price, typeString, item.brand, item.stock, item.duration
        ).enqueue(object : Callback<SimpleResponse> {
            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                loadDataFromServer()
            }
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {}
        })
    }
}