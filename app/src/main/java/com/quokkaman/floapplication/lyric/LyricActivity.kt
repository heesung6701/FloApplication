package com.quokkaman.floapplication.lyric

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import androidx.lifecycle.ViewModelProvider
import com.quokkaman.floapplication.R
import com.quokkaman.floapplication.contoller.MediaPlayerServiceController
import com.quokkaman.floapplication.databinding.ActivityLyricBinding
import com.quokkaman.floapplication.model.Song
import com.quokkaman.floapplication.viewmodel.MediaControllerViewModel
import com.quokkaman.floapplication.viewmodel.SeekbarViewModel

class LyricActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLyricBinding

    private lateinit var seekbarViewModel: SeekbarViewModel
    private lateinit var mediaControllerViewModel: MediaControllerViewModel

    private var serviceController = MediaPlayerServiceController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLyricBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val song: Song? = intent.getParcelableExtra(Song.INTENT_KEY)
        if (song == null) {
            finish()
        }

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