package com.quokkaman.floapplication.model

import android.os.Parcel
import android.os.Parcelable

data class Song(
    val singer: String,
    val album: String,
    val title: String,
    val duration: Int,
    val fileUrl: String,
    val imageUrl: String,
    val lyricLineList: List<LyricLine>
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(LyricLine)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(singer)
        parcel.writeString(album)
        parcel.writeString(title)
        parcel.writeInt(duration)
        parcel.writeString(fileUrl)
        parcel.writeString(imageUrl)
        parcel.writeTypedList(lyricLineList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val INTENT_KEY = "SONG"

        @JvmField
        val CREATOR = object: Parcelable.Creator<Song> {
            override fun createFromParcel(parcel: Parcel): Song {
                return Song(parcel)
            }

            override fun newArray(size: Int): Array<Song?> {
                return arrayOfNulls(size)
            }
        }
    }
}