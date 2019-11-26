package com.example.alarmmanagerexample

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


const val REQUEST_CODE = 5001
const val TIME_REPEATING = 7000 // min 5000
const val FLAG = 0

class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        println("MyBroadcastReceiver.onReceive Alarm...")

        val intent = Intent(context, MyBroadcastReceiver::class.java)
        val pIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, FLAG)
        val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + TIME_REPEATING,
            pIntent
        )
    }
}