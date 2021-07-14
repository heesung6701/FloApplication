package com.quokkaman.floapplication.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.quokkaman.floapplication.R
import com.quokkaman.floapplication.model.Song

class MusicInfoViewModel : ViewModel() {

    private val songLiveData = MutableLiveData<Song>()

    val titleLiveData: LiveData<String> = Transformations.map(songLiveData) { song -> song.title }
    val singerLiveData: LiveData<String> = Transformations.map(songLiveData) { song -> song.singer }
    val imageLiveData: LiveData<String> =
        Transformations.map(songLiveData) { song -> song.imageUrl }

    fun update(song: Song) {
        songLiveData.value = song
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.tv_singer -> {

            }
        }
    }
}