package com.quokkaman.floapplication.player

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import androidx.lifecycle.ViewModelProvider
import com.quokkaman.floapplication.contoller.MediaPlayerServiceController
import com.quokkaman.floapplication.databinding.ActivityPlayerBinding
import com.quokkaman.floapplication.dto.SongDTO
import com.quokkaman.floapplication.lyric.LyricActivity
import com.quokkaman.floapplication.model.Song
import com.quokkaman.floapplication.player.viewmodel.MusicInfoViewModel
import com.quokkaman.floapplication.player.viewmodel.MusicLyricViewModel
import com.quokkaman.floapplication.repository.SongRepository
import com.quokkaman.floapplication.viewmodel.MediaControllerViewModel
import com.quokkaman.floapplication.viewmodel.SeekbarViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private val songRepository = SongRepository
    private val disposable = CompositeDisposable()
    private lateinit var musicInfoViewModel: MusicInfoViewModel
    private lateinit var musicLyricViewModel: MusicLyricViewModel
    private lateinit var seekbarViewModel: SeekbarViewModel
    private lateinit var mediaControllerViewModel: MediaControllerViewModel

    private var draggingSeekbar = false
    private var fetchedSong: Song? = null

    private var serviceController = MediaPlayerServiceController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        musicInfoViewModel = ViewModelProvider(this).get(MusicInfoViewModel::class.java)
        musicLyricViewModel = ViewModelProvider(this).get(MusicLyricViewModel::class.java)
        seekbarViewModel = ViewModelProvider(this).get(SeekbarViewModel::class.java)
        mediaControllerViewModel =
            ViewModelProvider(this).get(MediaControllerViewModel::class.java)

        binding.musicInfoViewModel = musicInfoViewModel
        binding.musicLyricViewModel = musicLyricViewModel
        binding.seekbarViewModel = seekbarViewModel
        binding.mediaControllerViewModel = mediaControllerViewModel
        binding.lifecycleOwner = this

        fetchMusicInfo()
        mediaControllerViewModel.playingLiveData.observe(this, {
            if (it) {
                serviceController.play()
            } else {
                serviceController.pause()
            }
        })
        binding.itemSeekbar.seekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                p0?.progress = p1
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                draggingSeekbar = true
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                draggingSeekbar = false
                val seekBar = p0 ?: return
                serviceController.seekTo(seekBar.progress)
                mediaControllerViewModel.play()
            }
        })

        binding.itemLyric.root.setOnClickListener {
            val intent = Intent(this, LyricActivity::class.java)
            startActivity(intent)
        }

        serviceController.msecConsumer = Consumer { msec ->
            musicLyricViewModel.update(msec)
            if (!draggingSeekbar) {
                seekbarViewModel.update(msec)
            }
        }

        serviceController.durationConsumer = Consumer { duration ->
            mediaControllerViewModel.play()
            seekbarViewModel.durationLiveData.value = duration
            fetchedSong?.let {
                musicLyricViewModel.updateLyricLine(it.lyricLineList, duration)
            }
        }
    }

    private fun fetchMusicInfo() {
        disposable.add(
            songRepository.getSong()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<SongDTO>() {
                    override fun onSuccess(t: SongDTO?) {
                        val song = t?.toModel() ?: return
                        fetchedSong = song
                        musicInfoViewModel.update(song)

                        serviceController.setMusic(song.fileUrl)
                    }

                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                    }
                })
        )
    }

    override fun onResume() {
        super.onResume()
        serviceController.register()
    }

    override fun onPause() {
        super.onPause()
        serviceController.unregister()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceController.release()
        disposable.clear()
    }
}