package com.example.locationsample.data.gps

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.locationsample.MyApplication

const val REQUEST_CODE = 5001
const val TIME_REPEATING = 5000 // min 5000
const val FLAG = 0

class LocationTrackerManager {

    companion object {

        private val context = MyApplication.mInstance.applicationContext
        val trackerALA = LocationTrackerAndroidLocationAPI(context)

        fun getLocationAvailable() {
            trackerALA.checkIfLocationAvailable()
            println(
                "MyBroadcastReceiver.onReceive " +
                        "lat ${trackerALA.getLatitude()} " +
                        "long ${trackerALA.getLongitude()}"
            )
        }

        private val intent = Intent(context, LocationRepeatReceiver::class.java)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, FLAG)

        fun alarmTracking() {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + TIME_REPEATING,
                pIntent
            )
        }
    }

    fun startTracker() {
        if (trackerALA.isLocationEnabled) {
            // trackerALA!!.getCompleteAddressString(latitude, longitude)
            alarmTracking()
            println("MyBroadcastReceiver.onReceive Alarm set in $TIME_REPEATING")
        } else {
            trackerALA.askToOnLocation()  // show dialog box to user to enable location
        }
        // trackerGAC!!.getLastLocation()
    }

    fun stopTracker() {
        alarmManager.cancel(pIntent)
        println("MyBroadcastReceiver.onReceive Alarm... stop")
    }


}