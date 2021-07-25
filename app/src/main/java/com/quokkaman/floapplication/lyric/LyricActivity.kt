package com.quokkaman.floapplication.lyric

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.quokkaman.floapplication.R
import com.quokkaman.floapplication.contoller.MediaPlayerServiceController
import com.quokkaman.floapplication.databinding.ActivityLyricBinding
import com.quokkaman.floapplication.lyric.adapter.LyricLineAdapter
import com.quokkaman.floapplication.model.Song
import com.quokkaman.floapplication.viewmodel.MediaControllerViewModel
import com.quokkaman.floapplication.viewmodel.SeekbarViewModel

class LyricActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLyricBinding

    private lateinit var seekbarViewModel: SeekbarViewModel
    private lateinit var mediaControllerViewModel: MediaControllerViewModel

    private var serviceController = MediaPlayerServiceController(this)

    private var lyricLineAdapter = LyricLineAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLyricBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(!intent.hasExtra(Song.INTENT_KEY)) {
            finish()
            return
        }
        val song: Song = intent.getParcelableExtra(Song.INTENT_KEY)!!

        seekbarViewModel = ViewModelProvider(this).get(SeekbarViewModel::class.java).apply {
            mediaPlayerServiceController = serviceController
        }
        mediaControllerViewModel =
            ViewModelProvider(this).get(MediaControllerViewModel::class.java).apply {
                mediaPlayerServiceController = serviceController
            }

        binding.seekbarViewModel = seekbarViewModel
        binding.mediaControllerViewModel = mediaControllerViewModel
        binding.lifecycleOwner = this
        binding.itemSeekbar.seekbar.setOnSeekBarChangeListener(seekbarViewModel.onSeekBarChangeListener)
        binding.itemLyric.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.itemLyric.recyclerView.adapter = lyricLineAdapter
        lyricLineAdapter.submitList(song.lyricLineList)

        serviceController.msecConsumer = Consumer { msec ->
            seekbarViewModel.update(msec)
        }

        serviceController.durationConsumer = Consumer { duration ->
            seekbarViewModel.durationLiveData.value = duration
        }

        serviceController.getInfo()
    }

    override fun onResume() {
        super.onResume()
        serviceController.register()
    }

    override fun onPause() {
        super.onPause()
        serviceController.unregister()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.nothing, R.anim.slide_down_exit)
    }
}