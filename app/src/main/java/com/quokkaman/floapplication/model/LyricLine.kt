package com.quokkaman.floapplication.model

import android.os.Parcel
import android.os.Parcelable

data class LyricLine(
    val msec: Int,
    val lyrics: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(msec)
        parcel.writeString(lyrics)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LyricLine> {
        override fun createFromParcel(parcel: Parcel): LyricLine {
            return LyricLine(parcel)
        }

        override fun newArray(size: Int): Array<LyricLine?> {
            return arrayOfNulls(size)
        }
    }

}