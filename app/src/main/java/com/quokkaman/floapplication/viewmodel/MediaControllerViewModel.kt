package com.quokkaman.floapplication.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quokkaman.floapplication.R

class MediaControllerViewModel : ViewModel() {
    val play = MutableLiveData(false)
    val repeat = MutableLiveData(false)
    val shuffle = MutableLiveData(false)

    fun onClick(view: View) {
        when (view.id) {
            R.id.repeat_btn -> {
                repeat.value = repeat.value == false
            }
            R.id.prev_btn -> {

            }
            R.id.play_btn -> {
                play.value = play.value == false
            }
            R.id.next_btn -> {

            }
            R.id.shuffle_btn -> {
                shuffle.value = shuffle.value == false
            }
            R.id.list_btn -> {

            }
        }
    }
}