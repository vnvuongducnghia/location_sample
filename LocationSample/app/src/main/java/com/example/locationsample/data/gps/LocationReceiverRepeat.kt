package com.example.locationsample.data.gps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.locationsample.data.gps.LocationTrackerManager.Companion.alarmTracking
import com.example.locationsample.data.gps.LocationTrackerManager.Companion.getLocationAvailable


class LocationRepeatReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // println("MyBroadcastReceiver.onReceive Alarm...")
        getLocationAvailable()
        alarmTracking()
    }
}