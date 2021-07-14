package com.quokkaman.floapplication.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MediaControllerViewModel : ViewModel() {

    enum class RepeatType {
        REPEAT_TYPE_NONE,
        REPEAT_TYPE_ALL,
        REPEAT_TYPE_ONE
    }

    fun RepeatType.next(): RepeatType {
        val values = RepeatType.values()
        return values[(this.ordinal + 1) % values.size]
    }

    val playingLiveData = MutableLiveData(false)
    val repeatLivaData = MutableLiveData(RepeatType.REPEAT_TYPE_NONE)
    val shuffleLiveData = MutableLiveData(false)
    var mediaPlayer: MediaPlayer? = null

    fun play() {
        if (playingLiveData.value == true) return
        playingLiveData.value = true
        mediaPlayer?.start()
    }

    fun pause() {
        if (playingLiveData.value == false) return
        playingLiveData.value = false
        mediaPlayer?.pause()
    }

    fun toggleShuffle() {
        shuffleLiveData.value = (shuffleLiveData.value == false)
    }

    fun toggleRepeat() {
        repeatLivaData.value = repeatLivaData.value?.next() ?: RepeatType.REPEAT_TYPE_NONE
        mediaPlayer?.isLooping = repeatLivaData.value == RepeatType.REPEAT_TYPE_ONE
    }

    fun prevMusic() {

    }

    fun nextMusic() {

    }

    fun showList() {

    }
}