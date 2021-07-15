package com.quokkaman.floapplication.player.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quokkaman.floapplication.model.LyricLine


// TODO Use RecyclerView
class MusicLyricViewModel : ViewModel() {
    val lyric1LiveData = MutableLiveData<String>()
    val lyric1playingLiveData = MutableLiveData<Boolean>()
    val lyric2LiveData = MutableLiveData<String>()
    val lyric2playingLiveData = MutableLiveData<Boolean>()

    private val lyricLineList = ArrayList<LyricLine>()
    private var position = 0

    fun updateLyricLine(list: List<LyricLine>, duration: Int) {
        lyricLineList.clear()
        lyricLineList.add(LyricLine(-1, ""))
        lyricLineList.add(LyricLine(-1, ""))
        lyricLineList.addAll(list)
        lyricLineList.add(LyricLine(duration + 1, ""))
        lyricLineList.add(LyricLine(duration + 1, ""))
    }

    fun update(msec: Int) {
        while (position > 0 && msec < lyricLineList[position].msec) {
            position--
        }
        while (position < lyricLineList.size && lyricLineList[position].msec < msec) {
            position++
        }
        position -= 1
        lyric1LiveData.value = lyricLineList[position].lyrics
        lyric1playingLiveData.value =
            msec in lyricLineList[position].msec until lyricLineList[position + 1].msec
        lyric2LiveData.value = lyricLineList[position + 1].lyrics
        lyric2playingLiveData.value =
            msec in lyricLineList[position + 1].msec until lyricLineList[position + 2].msec
    }
}