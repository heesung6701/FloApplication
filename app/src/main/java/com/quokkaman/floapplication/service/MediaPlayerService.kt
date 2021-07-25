package com.quokkaman.floapplication.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.IOException


class MediaPlayerService : Service(), MediaPlayer.OnPreparedListener {

    companion object {
        const val ACTION_SET = "com.quokkaman.floapplication.ACTION_SET"
        const val ACTION_SET_BUNDLE_MEDIA = "media"

        const val ACTION_PLAY = "com.quokkaman.floapplication.ACTION_PLAY"
        const val ACTION_PAUSE = "com.quokkaman.floapplication.ACTION_PAUSE"
        const val ACTION_RELEASE = "com.quokkaman.floapplication.ACTION_RELEASE"
        const val ACTION_SEEKTO = "com.quokkaman.floapplication.ACTION_SEEKTO"
        const val ACTION_GET_INFO = "com.quokkaman.floapplication.ACTION_GET_INFO"

        const val MESSAGE_MSEC = "MediaPlayerService.MESSAGE_MSEC"
        const val MESSAGE_DURATION = "MediaPlayerService.MESSAGE_DURATION"

        const val EVENT_MSEC = "EVENT_MSEC"
        const val EVENT_SET_SOURCE = "EVENT_SET_SOURCE"
    }

    var mMediaPlayer: MediaPlayer? = null

    private val iBinder: IBinder = LocalBinder()
    private var isPlaying = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?:return super.onStartCommand(intent, flags, startId)
        Log.d(this::class.java.simpleName, "intent?.action:${intent.action}");
        when(intent.action) {
            ACTION_PLAY -> {
                playMedia()
            }
            ACTION_PAUSE -> {
                pauseMedia()
            }
            ACTION_RELEASE -> {
                releaseMedia()
            }
            ACTION_SET -> {
                val mediaFile = intent.extras?.getString(ACTION_SET_BUNDLE_MEDIA)
                if (mediaFile != null) {
                    initMediaPlayer(mediaFile)
                }
            }
            ACTION_SEEKTO -> {
                val msec = intent.getIntExtra(MESSAGE_MSEC, 0)
                seekTo(msec)
            }
            ACTION_GET_INFO -> {
                mMediaPlayer?.let {
                    sendMessage(EVENT_SET_SOURCE, MESSAGE_DURATION, it.duration)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder {
        return iBinder
    }

    override fun onPrepared(mediaPlayer: MediaPlayer?) {
        mediaPlayer?:return
        sendMessage(EVENT_SET_SOURCE, MESSAGE_DURATION, mediaPlayer.duration)
        playMedia()
    }

    private fun initMediaPlayer(mediaFile: String) {
        val mediaPlayer = MediaPlayer()
        mMediaPlayer = mediaPlayer
        mediaPlayer.reset()
        try {
            mediaPlayer.setDataSource(mediaFile)
        } catch (e: IOException) {
            e.printStackTrace()
            stopSelf()
        }
        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.prepareAsync()
    }

    private fun playMedia() {
        mMediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                isPlaying = true
                MusicThread().start()
            }
        }
    }

    private fun stopMedia() {
        mMediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                isPlaying = false
            }
        }
    }

    private var resumePosition: Int = 0
    private fun pauseMedia() {
        mMediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                isPlaying = false
                resumePosition = it.currentPosition
            }
        }
    }

    private fun resumeMedia() {
        mMediaPlayer?.let {
            if (!it.isPlaying) {
                it.seekTo(resumePosition)
                it.start()
                isPlaying = true
                MusicThread().start()
            }
        }
    }

    private fun releaseMedia() {
        mMediaPlayer?.release()
        mMediaPlayer = null
        isPlaying = false
    }

    private fun seekTo(msec: Int) {
        mMediaPlayer?.let {
            it.seekTo(msec)
        }
    }

    private fun sendMessage(event: String, message: String, data: Int){
        mMediaPlayer?.let {
            val intent = Intent(event).apply {
                putExtra(message, data)
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }

    inner class MusicThread : Thread() {

        var recent = -1

        override fun run() {
            while (isPlaying) {
                if(mMediaPlayer?.isPlaying == true) {
                    val msec = mMediaPlayer?.currentPosition ?: continue
                    if (recent == msec) continue
                    recent = msec
                    sendMessage(EVENT_MSEC, MESSAGE_MSEC, msec)
                }
            }
        }
    }

    inner class LocalBinder : Binder() {
        val service: MediaPlayerService
            get() = this@MediaPlayerService
    }
}