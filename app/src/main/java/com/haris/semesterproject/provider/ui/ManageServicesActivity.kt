package com.haris.semesterproject.provider.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.haris.semesterproject.R
import com.haris.semesterproject.network.RetrofitClient
import com.haris.semesterproject.provider.data.ServiceListResponse
import com.haris.semesterproject.provider.data.SimpleResponse
import com.haris.semesterproject.provider.adapter.ServicePartAdapter
import com.haris.semesterproject.provider.data.ItemType
import com.haris.semesterproject.provider.data.ServiceItem
import com.haris.semesterproject.utils.ProviderNavigation
import com.haris.semesterproject.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageServicesActivity : AppCompatActivity(), AddItemBottomSheet.OnItemAddedListener {

    // Lists
    private lateinit var adapter: ServicePartAdapter
    private val allItems = mutableListOf<ServiceItem>() // Stores data fetched from Server
    private var currentType = ItemType.SERVICE // Default tab

    // UI Components
    private lateinit var btnServices: Button
    private lateinit var btnParts: Button
    private lateinit var btnAddNew: Button
    private lateinit var rvList: RecyclerView
    private lateinit var btnBack: ImageView

    // Session Manager to get Logged in Provider ID
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_services)

        // 1. Initialize Session Manager
        sessionManager = SessionManager(this)

        // 2. Setup Views & Adapter
        setupViews()

        // Initialize Adapter with a Delete Callback
        adapter = ServicePartAdapter(mutableListOf()) { itemToDelete ->
            deleteItemFromServer(itemToDelete)
        }

        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = adapter

        // 3. Load Real Data from XAMPP Server
        loadDataFromServer()

        // 4. Click Listeners
        btnServices.setOnClickListener { switchTab(ItemType.SERVICE) }
        btnParts.setOnClickListener { switchTab(ItemType.PART) }
        btnBack.setOnClickListener { finish() }

        btnAddNew.setOnClickListener {
            // Open Bottom Sheet to Add Item
            val bottomSheet = AddItemBottomSheet.newInstance(currentType)
            bottomSheet.show(supportFragmentManager, "AddItemSheet")
        }

        // Inside onCreate
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.providerBottomNav)
        ProviderNavigation.setup(this, bottomNav, R.id.navigation_services)
    }

    private fun setupViews() {
        btnServices = findViewById(R.id.btnTabServices)
        btnParts = findViewById(R.id.btnTabParts)
        btnAddNew = findViewById(R.id.btnAddNew)
        rvList = findViewById(R.id.recyclerView)
        btnBack = findViewById(R.id.btnBack)

        // Initial Tab UI State
        highlightTab(btnServices, btnParts)
        btnAddNew.text = "+ Add New Service"
    }

    // --- BACKEND: FETCH DATA ---
    private fun loadDataFromServer() {
        val providerId = sessionManager.fetchUserId()

        if (providerId == -1) {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.api.getProviderServices(providerId).enqueue(object : Callback<ServiceListResponse> {
            override fun onResponse(call: Call<ServiceListResponse>, response: Response<ServiceListResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!

                    if (!apiResponse.error) {
                        // 1. Clear old list
                        allItems.clear()
                        // 2. Add new data from server
                        allItems.addAll(apiResponse.data)
                        // 3. Refresh UI
                        switchTab(currentType)
                    } else {
                        Toast.makeText(this@ManageServicesActivity, "No services found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ManageServicesActivity, "Server Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ServiceListResponse>, t: Throwable) {
                Toast.makeText(this@ManageServicesActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("ManageServices", t.message.toString())
            }
        })
    }

    // --- BACKEND: ADD ITEM ---
    override fun onItemAdded(item: ServiceItem) {
        val providerId = sessionManager.fetchUserId()

        // Determine correct type string for Database enum
        val typeString = if (item.type == ItemType.SERVICE) "SERVICE" else "PART"

        RetrofitClient.api.addServiceItem(
            providerId = providerId,
            name = item.name,
            category = item.category,
            price = item.price,
            type = typeString,
            brand = item.brand,
            stock = item.stock,
            duration = item.duration
        ).enqueue(object : Callback<SimpleResponse> {
            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    if (!response.body()!!.error) {
                        Toast.makeText(applicationContext, "Item Added Successfully!", Toast.LENGTH_SHORT).show()
                        // Reload data to show the new item
                        loadDataFromServer()
                    } else {
                        Toast.makeText(applicationContext, response.body()!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Failed to connect: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- BACKEND: DELETE ITEM (Placeholder logic) ---
    private fun deleteItemFromServer(item: ServiceItem) {
        // TODO: Create a delete_service.php API and add it to Retrofit Interface
        // For now, we just remove it locally to show UI update

        Toast.makeText(this, "Delete feature coming soon (Backend needed)", Toast.LENGTH_SHORT).show()

        /* // Future Implementation:
        RetrofitClient.api.deleteService(item.id).enqueue(object : Callback<SimpleResponse> {
             // onResponse success -> loadDataFromServer()
        })
        */
    }

    // --- UI LOGIC ---
    private fun switchTab(type: ItemType) {
        currentType = type

        // Update Buttons Visuals
        if (type == ItemType.SERVICE) {
            highlightTab(btnServices, btnParts)
            btnAddNew.text = "+ Add New Service"
        } else {
            highlightTab(btnParts, btnServices)
            btnAddNew.text = "+ Add New Spare Part"
        }

        // Filter the 'allItems' list by Type and update RecyclerView
        val filteredList = allItems.filter { it.type == type }
        adapter.updateList(filteredList)
    }

    private fun highlightTab(selected: Button, unselected: Button) {
        val maroon = ContextCompat.getColor(this, R.color.primary_maroon) // Make sure this color exists in colors.xml

        selected.backgroundTintList = ColorStateList.valueOf(maroon)
        selected.setTextColor(Color.WHITE)

        unselected.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        unselected.setTextColor(Color.BLACK)
    }

}