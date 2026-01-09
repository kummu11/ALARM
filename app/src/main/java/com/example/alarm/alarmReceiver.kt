package com.example.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.icu.text.DateFormat
import android.util.Log


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarm received")

        val activityIntent = Intent(context, AlarmActivity::class.java)
        activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NO_USER_ACTION

        //to display time on ringing screen :
        val calendar = java.util.Calendar.getInstance()
        val hour = calendar.get(java.util.Calendar.HOUR)
        val minute = calendar.get(java.util.Calendar.MINUTE)
        val amPm = if (calendar.get(java.util.Calendar.AM_PM) == java.util.Calendar.AM) "AM" else "PM"


        val displayHour = if(hour == 0)12 else hour
        val displayMinute = String.format("%02d", minute)
        activityIntent.putExtra("time", "$displayHour:$displayMinute")
        activityIntent.putExtra("amPm", amPm)

        //DATE/DAY :
        val dayFormat =  java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault())
        val DateFormat = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())

        val currentDay = dayFormat.format(calendar.time)
        val currentDate = DateFormat.format(calendar.time)

        activityIntent.putExtra("day", currentDay)
        activityIntent.putExtra("Date", currentDate)


        Log.d("AlarmReceiver", "Starting AlarmActivity")

        val taskType = intent.getStringExtra("TASK_TYPE") ?: "None"
        val target = intent.getIntExtra("TASK_TARGET", 0)
        activityIntent.putExtra("TASK_TYPE", taskType)
        activityIntent.putExtra("TASK_TARGET", target)


        context.startActivity(activityIntent)

    }
}