package com.example.playaudio

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var audioServiceBinder: AudioServiceBinder
    private lateinit var audioProgressUpdateHandler: Handler


    // This service connection object is the bridge between activity and background service.
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // Cast and assign background service's onBind method returned iBander object.
            audioServiceBinder = service as AudioServiceBinder
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Play Audio Use Background Service"

        // Bind background audio service when activity is created.
        bindAudioService()

        val audioFileUrl = "http://www.dev2qa.com/demo/media/test.mp3"

        // Show web audio file url in the text view.
        audio_file_url_text_view.text = "Audio File Url.\r\n $audioFileUrl"

        // Click this button to start play audio in a background service.
        start_audio_in_background.setOnClickListener {
            // Set web audio file url
            audioServiceBinder.setAudioFileUrl(audioFileUrl)

            // Web audio is a stream audio.
            audioServiceBinder.setStreamAudio(true)

            // Set application context.
            audioServiceBinder.setContext(applicationContext)

            // Initialize audio progress bar updater handler object.
            createAudioProgressbarUpdater()
            audioServiceBinder.setAudioProgressUpdateHandler(audioProgressUpdateHandler)

            // Start audio in background service.
            audioServiceBinder.startAudio()

            play_audio_in_background_service_progressbar.visibility = View.VISIBLE

            Toast.makeText(applicationContext, "Start play web audio file.", Toast.LENGTH_LONG)
                .show()

        }

        // Click this button to pause the audio played in background service.
        pause_audio_in_background.setOnClickListener {
            audioServiceBinder.pauseAudio()
            Toast.makeText(applicationContext, "Play web audio file is paused.", Toast.LENGTH_LONG)
                .show()
        }

        // Click this button to stop the media player in background service.
        stop_audio_in_background.setOnClickListener {
            audioServiceBinder.stopAudio()
            play_audio_in_background_service_progressbar.visibility = View.INVISIBLE
            Toast.makeText(applicationContext, "Stop play web audio file.", Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onDestroy() {
        // Unbound background audio service when activity is destroyed.
        unBoundAudioService()
        super.onDestroy()
    }

    // Bind background service with caller activity. Then this activity can use
    // background service's AudioServiceBinder instance to invoke related methods.
    private fun bindAudioService() {
        val intent = Intent(this, AudioService::class.java)
        // Below code will invoke serviceConnection's onServiceConnected method.
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    // Unbound background audio service with caller activity.
    private fun unBoundAudioService() {
        unbindService(serviceConnection)
    }

    // Create audio player progressbar updater.
    // This updater is used to update progressbar to reflect audio play process.
    private fun createAudioProgressbarUpdater() {
        // Initialize audio progress handler.

        audioProgressUpdateHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                // The update process message is sent from AudioServiceBinder class's thread object.
                if (msg.what == UPDATE_AUDIO_PROGRESS_BAR) {

                    // Calculate the percentage.
                    val currProgress = audioServiceBinder.getAudioProgress()
                    // Update progressbar. Make the value 10 times to show more clear UI change.
                    play_audio_in_background_service_progressbar.progress = currProgress * 10

                }
            }
        }

    }
}
