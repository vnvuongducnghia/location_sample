package com.example.alarmmanagerexample

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, MyBroadcastReceiver::class.java)
        val pIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, FLAG)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        button.setOnClickListener {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + TIME_REPEATING,
                pIntent
            )
            println("MyBroadcastReceiver.onReceive Alarm set in $TIME_REPEATING")
        }

        btnStop.setOnClickListener {
            alarmManager.cancel(pIntent)
            println("MyBroadcastReceiver.onReceive Alarm... stop")
        }
    }


    /*
    // alarmManager.setRepeating
    The documentation needs to be updated. As of I think Android 5.1 (API version 22) there is a minimum period of 1 minute for repeating alarms, and alarms cannot be set less than 5 seconds in the future.
    If you need to do work within one minute, just set the alarm directly, then set the next one from that alarm's handler, etc.
    If you need to do work within 5 seconds, post it to a Handler instead of using the alarm manager?
    This seems pretty accurate based on empirical testing. I've tested a number of devices running 5.0, 5.1 and 6.x.
    Setting a 15-second repeating alarm generates repeats from 15 seconds to 10 minutes apart, on different devices. Samsung devices seem to favor 5 minute repeats. This is definitely broken, it definitely does NOT match the documentation (which specifically states that if you target API < 19 the alarm is delivered exactly as pre-API 19, and it is definitely manufacturer-dependent. Best solution seems to be to set one alarm and then schedule each one after the previous one triggers.
    *
    ELAPSED_REALTIME—Fires the pending intent based on the amount of time since the device was booted, but doesn’t wake up the device. The elapsed time includes any time during which the device was asleep.
    ELAPSED_REALTIME_WAKEUP—Wakes up the device and fires the pending intent after a specified length of time has elapsed since device boot.
    RTC—Fires the pending intent at the specified time as per Wall Clock but does not wake up the device.
    RTC_WAKEUP—Wakes up the device to fire the pending intent at the specified time as per Wall Clock.
    *
    One more important factor while creating a pending intent is the flag passed in as the 4th parameter. Flags define the behavior of a PendingIntent and in turn the behavior of that Alarm.
    1. FLAG_CANCEL_CURRENT Flag indicating that if the described PendingIntent already exists, the current one should be canceled before generating a new one.
    2. FLAG_IMMUTABLE Flag indicating that the created PendingIntent should be immutable.
    3. FLAG_NO_CREATE Flag indicating that if the described PendingIntent does not already exist, then simply return null instead of creating it.
    4.FLAG_ONE_SHOT Flag indicating that this PendingIntent can be used only once.
    5. FLAG_UPDATE_CURRENT Flag indicating that if the described PendingIntent already exists, then keep it but replace its extra data with what is in this new Intent.
    */


}
