package com.quokkaman.floapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SeekbarViewModel : ViewModel() {
    val progressLiveData = MutableLiveData<Int>()
    val durationLiveData = MutableLiveData<Int>()
    val playSecondLiveData = MutableLiveData<Int>()

    fun update(msec: Int) {
        if (progressLiveData.value == msec) return
        progressLiveData.value = msec
        val sec = msec / 1000
        if (playSecondLiveData.value == sec) return
        playSecondLiveData.value = sec
    }
}