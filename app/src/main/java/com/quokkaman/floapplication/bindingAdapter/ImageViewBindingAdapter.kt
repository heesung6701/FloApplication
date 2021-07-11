package com.quokkaman.floapplication.bindingAdapter

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.quokkaman.floapplication.R

object ImageViewBindingAdapter {

    @BindingAdapter("imageUrl", "error")
    @JvmStatic
    fun imageLoad(imageView: ImageView, url: String?, error: Drawable) {
        if(url == null){
            imageView.setBackgroundResource(R.drawable.splash)
            return
        }
        Glide.with(imageView.context)
            .load(url)
            .error(error)
            .into(imageView)
    }
}