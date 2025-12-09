package com.haris.semesterproject.utils

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.haris.semesterproject.R
import com.haris.semesterproject.provider.ui.*

object ProviderNavigation {

    fun setup(activity: Activity, bottomNav: BottomNavigationView, selectedId: Int) {
        // 1. Set the correct item as selected
        bottomNav.selectedItemId = selectedId

        // 2. Handle Clicks
        bottomNav.setOnItemSelectedListener { item ->
            // If the user clicks the tab they are currently on, do nothing
            if (item.itemId == selectedId) {
                return@setOnItemSelectedListener true
            }

            // Navigate to the correct activity
            val intent: Intent? = when (item.itemId) {
                R.id.navigation_home -> Intent(activity, ProviderDashboardActivity::class.java)
                R.id.navigation_services -> Intent(activity, ManageServicesActivity::class.java)
                R.id.navigation_bookings -> Intent(activity, IncomingBookingsActivity::class.java)
                R.id.navigation_earnings -> Intent(activity, EarningsActivity::class.java)
                R.id.navigation_profile -> Intent(activity, WorkshopProfileActivity::class.java)
                else -> null
            }

            if (intent != null) {
                activity.startActivity(intent)
                // Remove animation for a smooth "tab switch" feel
                activity.overridePendingTransition(0, 0)
                // Optional: Finish the current activity to keep the back stack clean
                // activity.finish()
            }
            true
        }
    }
}