package com.example.unboundintentservice

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


const val REQUEST_CODE = 12345

class MainActivity : AppCompatActivity() {

    private var cashbackReciver: ClashbackReciver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerCashbackReceiver()

        txtHello.setOnClickListener {
            startCashbackService()
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(cashbackReciver)
    }

    private fun scheduleAlarm() {
        val intent = Intent(applicationContext, CashbackIntentService::class.java)
        val pIntent = PendingIntent.getBroadcast(
            this,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Setup periodic alarm every half hour from this point onwards.
        val firstMillis = System.currentTimeMillis() // alarm is set right away
        val alarm = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            firstMillis,
            AlarmManager.INTERVAL_HALF_HOUR,
            pIntent
        )

    }

    fun cancelAlarm() {
        val intent = Intent(applicationContext, CashbackIntentService::class.java)
        val pIntent = PendingIntent.getBroadcast(
            this, REQUEST_CODE,
            intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarm =
            this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.cancel(pIntent)
    }

    private fun startCashbackService() {
        val intent = Intent()
        intent.setClass(this, CashbackIntentService::class.java)
        intent.putExtra("cashback_cat", "fashion")
        startService(intent)
    }

    private fun registerCashbackReceiver() {
        cashbackReciver = ClashbackReciver()
        val intentFliter = IntentFilter()
        intentFliter.addAction(CASHBACK_INFO)
        registerReceiver(cashbackReciver, intentFliter)
    }

    inner class ClashbackReciver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val cbInfo = intent?.getStringExtra("cashback")
            txtHello.text = cbInfo
        }
    }
}
