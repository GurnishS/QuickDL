package com.solvynix.quickdl.data.sharedprefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    // Save a setting (key-value pair)
    fun saveSetting(key: String, value: String) {
        sharedPreferences.edit() { putString(key, value) }
    }

    // Retrieve a setting (key-value pair)
    fun getSetting(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    // Example to store a boolean setting
    fun saveBooleanSetting(key: String, value: Boolean) {
        sharedPreferences.edit() { putBoolean(key, value) }
    }

    // Example to retrieve a boolean setting
    fun getBooleanSetting(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }
}
