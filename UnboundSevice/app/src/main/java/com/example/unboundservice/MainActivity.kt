package com.example.unboundservice

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
 
        btnPlayMusic.setOnClickListener {
            // Start service
            val i = Intent(this, MyService::class.java)
            i.putExtra("KEY1", "Value to be used by the service")
            startService(i)
        }

        // Stop serivce
        btnStop.setOnClickListener {
            val i = Intent(this, MyService::class.java)
            stopService(i)
        }
    }
}
