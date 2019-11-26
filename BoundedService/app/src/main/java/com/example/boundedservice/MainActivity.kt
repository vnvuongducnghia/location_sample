package com.example.boundedservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ServiceConnection {

    private var s: MyService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        s = MyService()

        btnGetList.setOnClickListener {
            println("MainActivity.onCreate s ${s!!.getWordList()?.size}")

            val service = Intent(applicationContext, MyService::class.java)
            applicationContext.startService(service)
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, MyService::class.java)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    override fun onPause() {
        super.onPause()
        unbindService(this)
    }

    //region Service Connection
    override fun onServiceDisconnected(name: ComponentName?) {
        println("MainActivity.onServiceDisconnected")
        s = null
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        println("MainActivity.onServiceConnected")
        val b = service as MyService.MyBinder
        s = b.getService()
    }
    //endregion
}
