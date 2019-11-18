package com.example.locationsample

import android.annotation.SuppressLint
import android.app.Application

@SuppressLint("Registered")
class MyApplication : Application() {

    companion object {
        lateinit var mInstance: MyApplication
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }


}