package com.quokkaman.floapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MusicLyricViewModel: ViewModel() {
    val lyric1 = MutableLiveData<String>()
    val lyric1playing = MutableLiveData<Boolean>()
    val lyric2 = MutableLiveData<String>()
    val lyric2playing = MutableLiveData<Boolean>()
}