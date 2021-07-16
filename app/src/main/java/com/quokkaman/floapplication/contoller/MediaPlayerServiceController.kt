package com.quokkaman.floapplication.contoller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.util.Consumer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.quokkaman.floapplication.service.MediaPlayerService
import java.lang.Exception

class MediaPlayerServiceController(val context: Context) {

    var msecConsumer: Consumer<Int>? = null
    var durationConsumer: Consumer<Int>? = null

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val intent = p1?:return
            when(intent.action) {
                MediaPlayerService.EVENT_MSEC -> {
                    val msec = intent.getIntExtra(MediaPlayerService.MESSAGE_MSEC, 0)
                    msecConsumer?.accept(msec)
                }
                MediaPlayerService.EVENT_SET_SOURCE -> {
                    val duration = intent.getIntExtra(MediaPlayerService.MESSAGE_DURATION, 0)
                    durationConsumer?.accept(duration)
                }
                else ->{
                    throw Exception(intent.action+"")
                }
            }
        }
    }

    fun setMusic(fileUrl: String) {
        context.apply {
            startService(
                Intent(
                    this,
                    MediaPlayerService::class.java
                ).apply {
                    action = MediaPlayerService.ACTION_SET
                    putExtra("media", fileUrl)
                })
        }
    }

    fun register() {
        LocalBroadcastManager.getInstance(context).registerReceiver(
            mMessageReceiver, IntentFilter().apply {
                addAction(MediaPlayerService.EVENT_MSEC)
                addAction(MediaPlayerService.EVENT_SET_SOURCE)
            }
        )
    }

    fun unregister(){
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver)
    }


    fun play() {
        context.apply {
            startService(Intent(applicationContext, MediaPlayerService::class.java).apply {
                action = MediaPlayerService.ACTION_PLAY
            })
        }
    }

    fun pause() {
        context.apply {
            startService(Intent(this, MediaPlayerService::class.java).apply {
                action = MediaPlayerService.ACTION_PAUSE
            })
        }
    }

    fun seekTo(progress: Int) {
        context.apply {
            startService(Intent(applicationContext, MediaPlayerService::class.java).apply {
                action = MediaPlayerService.ACTION_SEEKTO
                putExtra(MediaPlayerService.MESSAGE_MSEC, progress)
            })
        }
    }

    fun release() {
        context.apply {
            startService(Intent(this, MediaPlayerService::class.java).apply {
                action = MediaPlayerService.ACTION_RELEASE
            })
        }
    }
}