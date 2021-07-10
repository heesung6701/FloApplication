package com.quokkaman.floapplication.dto

import com.quokkaman.floapplication.model.Line
import com.quokkaman.floapplication.model.Song

data class SongDTO(
    val singer: String?,
    val album: String?,
    val title: String?,
    val duration: Int?,
    val image: String?,
    val file: String?,
    val lyrics: String?
) {
    fun toModel(): Song {
        val lines = lyrics!!.split('\n').map {
            val match = Regex("^\\[(\\d{2}):(\\d{2}):(\\d{3})](.*)$").find(it)!!
            val (minute, second, millisecond, lyrics) = match.destructured
            val longTime: Long =
                (minute.toInt() * 60 + second.toInt()).toLong() * 1000 + millisecond.toInt()
            Line(longTime, lyrics)
        }
        return Song(singer!!, album!!, title!!, duration!!, file!!, image!!, lines)
    }
}