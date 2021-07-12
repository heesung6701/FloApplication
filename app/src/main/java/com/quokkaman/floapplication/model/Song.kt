package com.quokkaman.floapplication.model

data class Song(
    val singer: String,
    val album: String,
    val title: String,
    val duration: Int,
    val fileUrl: String,
    val imageUrl: String,
    val lyricLineList: List<LyricLine>
)