package com.example.alarm

import android.content.Context
import androidx.compose.ui.input.key.type
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AlarmStorage {
        private const val PREF_NAME = "alarm_prefs"
        private const val KEY_ALARMS = "saved_alarms"
        private val gson = Gson()

        fun saveAlarms(context: Context, alarms: List<AlarmItem>) {
            val json = gson.toJson(alarms) // Convert List -> String
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_ALARMS, json).apply() // Save to Disk
        }

        fun loadAlarms(context: Context): MutableList<AlarmItem> {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val json = prefs.getString(KEY_ALARMS, null)

            return if (json != null) {
                val type = object : TypeToken<MutableList<AlarmItem>>() {}.type
                gson.fromJson(json, type) // Convert String -> List
            } else {
                mutableListOf() // Return empty list if nothing saved
            }
        }
    }
