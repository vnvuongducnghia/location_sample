package com.example.playaudio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.text.format.DateUtils
import java.io.IOException

// This is the message signal that inform audio progress updater to update audio progress.
const val UPDATE_AUDIO_PROGRESS_BAR = 1

class AudioServiceBinder : Binder() {

    // Save local audio file uri ( local storage file. ).
    private var audioFileUri: Uri? = null

    // Save web audio file url.
    private var audioFileUrl: String = ""

    // Check is stream audio.
    private var streamAudio = false

    // Media player that play audio.
    private lateinit var audioPlayer: MediaPlayer

    // Caller activity context, used when play local audio file.
    private var context: Context? = null

    // This handler object is a reference to the caller activity's Handler.
    // In the caller activity's handler, it will update the audio play progress.
    private var audioProgressUpdateHandler: Handler? = null

    fun getContext(): Context? {
        return context
    }

    fun setContext(context: Context?) {
        this.context = context
    }

    fun getAudioFileUrl(): String? {
        return audioFileUrl
    }

    fun setAudioFileUrl(audioFileUrl: String?) {
        this.audioFileUrl = audioFileUrl!!
    }

    fun isStreamAudio(): Boolean {
        return streamAudio
    }

    fun setStreamAudio(streamAudio: Boolean) {
        this.streamAudio = streamAudio
    }

    fun getAudioFileUri(): Uri? {
        return audioFileUri
    }

    fun setAudioFileUri(audioFileUri: Uri?) {
        this.audioFileUri = audioFileUri
    }

    fun getAudioProgressUpdateHandler(): Handler? {
        return audioProgressUpdateHandler
    }

    fun setAudioProgressUpdateHandler(audioProgressUpdateHandler: Handler?) {
        this.audioProgressUpdateHandler = audioProgressUpdateHandler
    }

    // Start play audio.
    fun startAudio() {
        initAudioPlayer()
        audioPlayer.start()
    }

    // Pause playing audio.
    fun pauseAudio() {
        audioPlayer.pause()
    }

    // Stop play audio.
    fun stopAudio() {
        audioPlayer.stop()
        destroyAudioPlayer()
    }

    // Initialise audio player.
    private fun initAudioPlayer() {
        try {
            audioPlayer = MediaPlayer()
            if (!TextUtils.isEmpty(getAudioFileUrl())) {
                if (isStreamAudio()) {
                    audioPlayer.setAudioAttributes(
                        AudioAttributes
                            .Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                }
                audioPlayer.setDataSource(getAudioFileUrl())
            } else {
                audioPlayer.setDataSource(this.getContext()!!, this.getAudioFileUri()!!)
            }
            // audioPlayer.prepare()
            audioPlayer.prepareAsync()
            audioPlayer.setOnPreparedListener { mp ->
                val length = mp?.duration.let { 0 } // duration in time in millis
                val durationText = DateUtils.formatElapsedTime((length / 1000).toLong())
                // converting time in millis to minutes:second format eg 14:15 min
                println("AudioServiceBinder.onPrepared durationText $durationText")
            }

            // audioPlayer = MediaPlayer.create(this.getContext()!!, R.raw.tho_tinh_cuoi_mua_thu)

            val url = "http://www.dev2qa.com/demo/media/test.mp3"
            val mediaPlayer = MediaPlayer.create(this.getContext()!!, Uri.parse(url))
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.setOnBufferingUpdateListener { mp, percent ->
                println("AudioServiceBinder.initAudioPlayer mediaPlayer ${mediaPlayer.duration}")
            }


            // This thread object will send update audio progress message to caller activity every 1 secound.
            val updateAudioProgressThread = Thread {
                while (true) {
                    // Create update audio progress message.
                    val updateAudioProgressMsg = Message()
                    updateAudioProgressMsg.what = UPDATE_AUDIO_PROGRESS_BAR

                    // Send the message to caller activity's update audio progressbar handler object.
                    audioProgressUpdateHandler?.sendMessage(updateAudioProgressMsg)

                    // Sleep one second.
                    try {
                        Thread.sleep(3000)
                    } catch (ex: InterruptedException) {
                        ex.printStackTrace()
                    }
                }
            }

            // Run above thread object.
            updateAudioProgressThread.start()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    // Destroy audio player.
    private fun destroyAudioPlayer() {
        if (audioPlayer.isPlaying) {
            audioPlayer.stop()
        }
        audioPlayer.release()
    }

    // Return current audio play position.
    private fun getCurrentAudioPosition(): Int {
        return audioPlayer.currentPosition
    }

    // Return total audio file duration.
    private fun getTotalAudioDuration(): Int {
        return audioPlayer.duration
    }

    // Return current audio player progress value.
    fun getAudioProgress(): Int {
        var ret = 0
        val currAudioPosition = getCurrentAudioPosition()
        val totalAudioDuration = getTotalAudioDuration()
        if (totalAudioDuration > 0) {
            ret = (currAudioPosition * 100) / totalAudioDuration
        }
        return ret
    }
}