package com.quokkaman.floapplication.bindingAdapter

import android.widget.TextView
import androidx.databinding.BindingAdapter

object TimeBindingAdapter {

    @BindingAdapter("secondToString")
    @JvmStatic
    fun secondToString(textView: TextView, second: Int) {
        var remainSecond: Int = second
        val seconds = remainSecond % 60
        remainSecond /= 60
        val minutes = remainSecond % 60
        remainSecond /= 60
        val hours = remainSecond

        val timeStr = (if (hours > 0)
            String.format("%02d", hours)
        else "") + "${String.format("%02d", minutes)}:${String.format("%02d", seconds)}"

        textView.text = timeStr
    }
}