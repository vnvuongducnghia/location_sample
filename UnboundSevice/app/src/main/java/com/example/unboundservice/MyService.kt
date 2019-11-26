package com.example.unboundservice

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

/*Unbounded Service được sử dụng để thực hiện nhiệm vụ lâu dài và lặp đi lặp lại.
* Unbound Service được khởi động bởi gọi startService().
* Unbound Service bị dừng lại hoặc bị hủy bởi gọi một cách tường minh phương thức stopService().
* Unbound Service độc lập với thành phần đã khởi động nó.
* */

class MyService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        // Service nay la loai khong rang buoc 'UnBound'
        // Nen onBind nay khong bao gio duoc goi
        return null
    }

    var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        if (mediaPlayer == null)
            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.tho_tinh_cuoi_mua_thu)
    }

    // Start sticky: Tao lai dich vu khi da co du bo nho voi intent null.
    // Start not sticky: Khong can tao lai khi bo nho du
    // Start redeliver intent: Tao lai dich vu voi intent != null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null  ) {
            mediaPlayer?.start()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}