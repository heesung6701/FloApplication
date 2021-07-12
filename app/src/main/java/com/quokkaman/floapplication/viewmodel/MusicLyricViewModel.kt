package com.quokkaman.floapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quokkaman.floapplication.model.LyricLine


// TODO Use RecyclerView
class MusicLyricViewModel: ViewModel() {
    val lyric1 = MutableLiveData<String>()
    val lyric1playing = MutableLiveData<Boolean>()
    val lyric2 = MutableLiveData<String>()
    val lyric2playing = MutableLiveData<Boolean>()

    private val lyricLineList = ArrayList<LyricLine>()
    private var position = 0

    fun updateLyricLine(list : List<LyricLine>, duration: Int) {
        lyricLineList.clear()
        lyricLineList.add(LyricLine(-1, ""))
        lyricLineList.add(LyricLine(-1, ""))
        lyricLineList.addAll(list)
        lyricLineList.add(LyricLine(duration + 1, ""))
        lyricLineList.add(LyricLine(duration + 1, ""))
    }

    fun updateSecond(milliSecond: Int) {
        while(position > 0 && milliSecond < lyricLineList[position].millisecond) {
            position--
        }
        while(position < lyricLineList.size && lyricLineList[position].millisecond < milliSecond) {
            position++
        }
        position -= 1
        lyric1.value = lyricLineList[position].lyrics
        lyric1playing.value = milliSecond in lyricLineList[position].millisecond until lyricLineList[position+1].millisecond
        lyric2.value = lyricLineList[position + 1].lyrics
        lyric2playing.value = milliSecond in lyricLineList[position + 1].millisecond until lyricLineList[position+2].millisecond
    }
}