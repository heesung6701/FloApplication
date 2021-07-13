package com.quokkaman.floapplication.bindingAdapter

import android.widget.ImageButton
import androidx.databinding.BindingAdapter

object ControllerBindingAdapter {

    @BindingAdapter("active", "activeColor", "inactiveColor")
    @JvmStatic
    fun activeColor(imageButton: ImageButton, active: Boolean, activeColor: Int, inactiveColor: Int) {
        if(active) {
            imageButton.setColorFilter(activeColor)
        } else {
            imageButton.setColorFilter(inactiveColor)
        }
    }
}