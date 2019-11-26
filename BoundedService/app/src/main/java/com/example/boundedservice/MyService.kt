package com.example.boundedservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.util.*

/*Bounded Service được sử dụng để thực hiện nhiệm vụ ở nền (background) và giàng buộc với thành phần giao diện.
* Bounded Service được khởi động bởi gọi bindService().
* Bounded Service bị gỡ giàng buộc hoặc bị hủy bởi gọi unbindService().
* Bound Service phụ thuộc vào thành phần giao diện đã khởi động nó.
* */

class MyService : Service() { 

    inner class MyBinder : Binder() {
        fun getService(): MyService {
            return this@MyService
        }
    }

    private val binder = MyBinder()

    override fun onBind(intent: Intent?): IBinder? {
        println("MainActivity.onBind")
        return this.binder
    }

    override fun onRebind(intent: Intent?) {
        println("MainActivity.onRebind")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        println("MainActivity.onUnbind")
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        println("MainActivity.onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        addResultValues()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        println("MainActivity.onDestroy")
        super.onDestroy()
    }


    //region Result Values
    private val resultList: ArrayList<String> = ArrayList()
    private var counter = 1

    fun getWordList(): List<String?>? {
        return resultList
    }

    private fun addResultValues() {
        val random = Random()
        val input = listOf("Linux", "Android", "iPhone", "Windows7")
        resultList.add(input[random.nextInt(3)] + " " + counter++)
        if (counter == Int.MAX_VALUE) {
            counter = 0
        }
    }
    //endregion


}