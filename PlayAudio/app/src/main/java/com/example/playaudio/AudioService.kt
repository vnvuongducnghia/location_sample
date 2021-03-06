package com.example.playaudio

import android.app.Service
import android.content.Intent
import android.os.IBinder


class AudioService : Service() {

    private val audioServiceBinder = AudioServiceBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return audioServiceBinder
    }
}