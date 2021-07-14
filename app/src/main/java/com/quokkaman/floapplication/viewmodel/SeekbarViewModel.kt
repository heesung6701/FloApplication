package com.quokkaman.floapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SeekbarViewModel : ViewModel() {
    val progressLiveData = MutableLiveData<Int>()
    val durationLiveData = MutableLiveData<Int>()
    val playSecondLiveData = MutableLiveData<Int>()
}