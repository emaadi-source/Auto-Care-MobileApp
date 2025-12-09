package com.haris.semesterproject.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = prefs.edit()

    fun saveUser(id: Int, name: String, role: String) {
        editor.putInt("USER_ID", id)
        editor.putString("USER_NAME", name)
        editor.putString("USER_ROLE", role)
        editor.apply()
    }

    fun fetchUserId(): Int {
        return prefs.getInt("USER_ID", -1)
    }

    fun fetchUserRole(): String? {
        return prefs.getString("USER_ROLE", null)
    }

    fun fetchUserName(): String? {
        return prefs.getString("USER_NAME", "User")
    }

    // Support both names to prevent errors
    fun logout() {
        editor.clear()
        editor.apply()
    }

    fun clear() {
        logout()
    }
}