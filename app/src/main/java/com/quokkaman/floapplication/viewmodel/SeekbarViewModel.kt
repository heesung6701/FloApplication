package com.quokkaman.floapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SeekbarViewModel: ViewModel() {
    val playSecond = MutableLiveData<Int>()
    val totalSecond = MutableLiveData<Int>()
}