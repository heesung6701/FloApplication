package com.quokkaman.floapplication.viewmodel

import android.widget.SeekBar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quokkaman.floapplication.contoller.MediaPlayerServiceController

class SeekbarViewModel : ViewModel() {
    val progressLiveData = MutableLiveData<Int>()
    val durationLiveData = MutableLiveData<Int>()
    val playSecondLiveData = MutableLiveData<Int>()

    lateinit var mediaPlayerServiceController : MediaPlayerServiceController

    private var draggingSeekbar = false

    val onSeekBarChangeListener = object :
        SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            p0?.progress = p1
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
            draggingSeekbar = true
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
            draggingSeekbar = false
            val seekBar = p0 ?: return
            mediaPlayerServiceController.seekTo(seekBar.progress)
        }
    }
    fun update(msec: Int) {
        if (draggingSeekbar) return
        if (progressLiveData.value == msec) return
        progressLiveData.value = msec
        val sec = msec / 1000
        if (playSecondLiveData.value == sec) return
        playSecondLiveData.value = sec
    }
}