package com.quokkaman.floapplication.contoller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.util.Consumer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.quokkaman.floapplication.service.MediaPlayerService
import com.quokkaman.floapplication.service.MediaPlayerService.Companion.ACTION_GET_INFO
import com.quokkaman.floapplication.service.MediaPlayerService.Companion.ACTION_PAUSE
import com.quokkaman.floapplication.service.MediaPlayerService.Companion.ACTION_PLAY
import com.quokkaman.floapplication.service.MediaPlayerService.Companion.ACTION_RELEASE
import com.quokkaman.floapplication.service.MediaPlayerService.Companion.ACTION_SEEKTO
import com.quokkaman.floapplication.service.MediaPlayerService.Companion.ACTION_SET
import com.quokkaman.floapplication.service.MediaPlayerService.Companion.ACTION_SET_BUNDLE_MEDIA
import com.quokkaman.floapplication.service.MediaPlayerService.Companion.EVENT_MSEC
import com.quokkaman.floapplication.service.MediaPlayerService.Companion.EVENT_SET_SOURCE
import com.quokkaman.floapplication.service.MediaPlayerService.Companion.MESSAGE_DURATION
import com.quokkaman.floapplication.service.MediaPlayerService.Companion.MESSAGE_MSEC
import java.lang.Exception

class MediaPlayerServiceController(val context: Context) {

    var msecConsumer: Consumer<Int>? = null
    var durationConsumer: Consumer<Int>? = null

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val intent = p1 ?: return
            when (intent.action) {
                EVENT_MSEC -> {
                    val msec = intent.getIntExtra(MESSAGE_MSEC, 0)
                    msecConsumer?.accept(msec)
                }
                EVENT_SET_SOURCE -> {
                    val duration = intent.getIntExtra(MESSAGE_DURATION, 0)
                    durationConsumer?.accept(duration)
                }
                else -> {
                    throw Exception(intent.action + "")
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
                    action = ACTION_SET
                    putExtra(ACTION_SET_BUNDLE_MEDIA, fileUrl)
                })
        }
    }

    fun register() {
        LocalBroadcastManager.getInstance(context).registerReceiver(
            mMessageReceiver, IntentFilter().apply {
                addAction(EVENT_MSEC)
                addAction(EVENT_SET_SOURCE)
            }
        )
    }

    fun unregister() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver)
    }

    fun play() {
        context.apply {
            startService(Intent(this, MediaPlayerService::class.java).apply {
                action = ACTION_PLAY
            })
        }
    }

    fun pause() {
        context.apply {
            startService(Intent(this, MediaPlayerService::class.java).apply {
                action = ACTION_PAUSE
            })
        }
    }

    fun seekTo(progress: Int) {
        context.apply {
            startService(Intent(this, MediaPlayerService::class.java).apply {
                action = ACTION_SEEKTO
                putExtra(MESSAGE_MSEC, progress)
            })
        }
    }

    fun getInfo() {
        context.apply {
            startService(Intent(this, MediaPlayerService::class.java).apply {
                action = ACTION_GET_INFO
            })
        }
    }

    fun release() {
        context.apply {
            startService(Intent(this, MediaPlayerService::class.java).apply {
                action = ACTION_RELEASE
            })
        }
    }
}