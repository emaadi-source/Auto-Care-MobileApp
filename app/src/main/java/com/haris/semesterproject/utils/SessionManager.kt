package com.haris.semesterproject.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private var prefs: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = prefs.edit()

    // Save all user info including email
    fun saveUser(id: Int, name: String, email: String, role: String = "") {
        editor.putInt("USER_ID", id)
        editor.putString("USER_NAME", name)
        editor.putString("USER_EMAIL", email)
        editor.putString("USER_ROLE", role)
        editor.apply()
    }

    fun fetchUserId(): Int {
        return prefs.getInt("USER_ID", -1)
    }

    fun fetchUserName(): String? {
        return prefs.getString("USER_NAME", "User")
    }

    fun fetchUserEmail(): String? {
        return prefs.getString("USER_EMAIL", "user@example.com")
    }

    fun fetchUserRole(): String? {
        return prefs.getString("USER_ROLE", null)
    }

    fun logout() {
        editor.clear()
        editor.apply()
    }

    fun clear() {
        logout()
    }
}
