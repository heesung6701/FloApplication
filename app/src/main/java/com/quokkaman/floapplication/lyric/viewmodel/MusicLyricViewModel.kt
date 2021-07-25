package com.quokkaman.floapplication.lyric.viewmodel

import androidx.lifecycle.ViewModel
import com.quokkaman.floapplication.model.LyricLine

class MusicLyricViewModel : ViewModel() {

    private val lyricLineList = ArrayList<LyricLine>()
    private var position = 0

    fun updateLyricLine(list: List<LyricLine>, duration: Int) {
        lyricLineList.addAll(list)
    }

    fun update(msec: Int) {

    }
}