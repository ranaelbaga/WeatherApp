package com.example.project2.util

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("location_prefs", Context.MODE_PRIVATE)

    fun saveLocation(longitude: String, latitude: String) {
        prefs.edit().putString("longitude", longitude).putString("latitude", latitude).apply()
    }

    fun getLongitude(): String? = prefs.getString("longitude", null)

    fun getLatitude(): String? = prefs.getString("latitude", null)
}
