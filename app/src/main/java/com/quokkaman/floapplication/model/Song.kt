package com.quokkaman.floapplication.model

data class Song(
    val singer: String,
    val album: String,
    val title: String,
    val duration: Int,
    val fileUrl: String,
    val imageUrl: String,
    val lines: List<Line>
)

data class Line(
    val time: Long,
    val lyrics: String
)