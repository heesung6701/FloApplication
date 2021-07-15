package com.quokkaman.floapplication.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.quokkaman.floapplication.databinding.ActivityPlayerBinding
import com.quokkaman.floapplication.dto.SongDTO
import com.quokkaman.floapplication.repository.SongRepository
import com.quokkaman.floapplication.viewmodel.MediaControllerViewModel
import com.quokkaman.floapplication.viewmodel.MusicInfoViewModel
import com.quokkaman.floapplication.viewmodel.MusicLyricViewModel
import com.quokkaman.floapplication.viewmodel.SeekbarViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val MESSAGE_WHAT_MILLISECOND = 1

        const val KEY_MSEC = "millisecond"
    }

    private lateinit var binding: ActivityPlayerBinding

    private val songRepository = SongRepository
    private val disposable = CompositeDisposable()
    private lateinit var musicInfoViewModel: MusicInfoViewModel
    private lateinit var musicLyricViewModel: MusicLyricViewModel
    private lateinit var seekbarViewModel: SeekbarViewModel
    private lateinit var mediaControllerViewModel: MediaControllerViewModel

    private val mediaPlayer = MediaPlayer()

    private var isPlaying = false
    private var draggingSeekbar = false

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_WHAT_MILLISECOND -> {
                    val playMillisecond: Int = msg.data.getInt(KEY_MSEC)
                    musicLyricViewModel.update(playMillisecond)
                    if (!draggingSeekbar) {
                        seekbarViewModel.update(playMillisecond)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        musicInfoViewModel = ViewModelProvider(this).get(MusicInfoViewModel::class.java)
        musicLyricViewModel = ViewModelProvider(this).get(MusicLyricViewModel::class.java)
        seekbarViewModel = ViewModelProvider(this).get(SeekbarViewModel::class.java)
        mediaControllerViewModel =
            ViewModelProvider(this).get(MediaControllerViewModel::class.java).apply {
                mediaPlayer = this@PlayerActivity.mediaPlayer
            }

        binding.musicInfoViewModel = musicInfoViewModel
        binding.musicLyricViewModel = musicLyricViewModel
        binding.seekbarViewModel = seekbarViewModel
        binding.mediaControllerViewModel = mediaControllerViewModel
        binding.lifecycleOwner = this

        fetchMusicInfo()
        mediaControllerViewModel.playingLiveData.observe(this, {
            if (isPlaying == it) return@observe
            isPlaying = it
            if (isPlaying) {
                MusicThread().start()
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
                mediaPlayer.seekTo(seekBar.progress)
                mediaControllerViewModel.play()
            }

        })
    }

    private fun fetchMusicInfo() {
        disposable.add(
            songRepository.getSong()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<SongDTO>() {
                    override fun onSuccess(t: SongDTO?) {
                        val song = t?.toModel() ?: return
                        musicInfoViewModel.update(song)
                        mediaPlayer.setDataSource(t.file)
                        mediaPlayer.prepare()
                        val duration = mediaPlayer.duration
                        seekbarViewModel.durationLiveData.value = duration
                        musicLyricViewModel.updateLyricLine(song.lyricLineList, duration)

                        mediaControllerViewModel.play()
                    }

                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                    }
                })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        isPlaying = false
        disposable.clear()
    }

    inner class MusicThread : Thread() {

        var recent = -1

        override fun run() {
            while (isPlaying) {
                val msec = mediaPlayer.currentPosition
                if (recent == msec) continue
                recent = msec
                handler.sendMessage(Message().apply {
                    what = MESSAGE_WHAT_MILLISECOND
                    data.putInt(KEY_MSEC, msec)
                })
            }
        }
    }
}