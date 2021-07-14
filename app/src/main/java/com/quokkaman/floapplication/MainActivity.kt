package com.quokkaman.floapplication

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.quokkaman.floapplication.databinding.ActivityMainBinding
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

class MainActivity : AppCompatActivity() {

    companion object {
        const val MESSAGE_WHAT_SECOND = 1
        const val MESSAGE_WHAT_MILLISECOND = 2
    }

    private lateinit var binding: ActivityMainBinding

    private val songRepository = SongRepository
    private val disposable = CompositeDisposable()
    private lateinit var musicInfoViewModel: MusicInfoViewModel
    private lateinit var musicLyricViewModel: MusicLyricViewModel
    private lateinit var seekbarViewModel: SeekbarViewModel
    private lateinit var mediaControllerViewModel: MediaControllerViewModel

    private val mediaPlayer = MediaPlayer()

    private var isPlaying = false

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_WHAT_MILLISECOND -> {
                    val playMillisecond : Int = msg.data.getInt("millisecond")
                    musicLyricViewModel.updateSecond(playMillisecond)
                }
                MESSAGE_WHAT_SECOND -> {
                    val playSecond : Int = msg.data.getInt("second")
                    seekbarViewModel.playSecond.value = playSecond
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        musicInfoViewModel = ViewModelProvider(this).get(MusicInfoViewModel::class.java)
        musicLyricViewModel = ViewModelProvider(this).get(MusicLyricViewModel::class.java)
        seekbarViewModel = ViewModelProvider(this).get(SeekbarViewModel::class.java)
        mediaControllerViewModel = ViewModelProvider(this).get(MediaControllerViewModel::class.java)

        binding.musicInfoViewModel = musicInfoViewModel
        binding.musicLyricViewModel = musicLyricViewModel
        binding.seekbarViewModel = seekbarViewModel
        binding.mediaControllerViewModel = mediaControllerViewModel
        binding.lifecycleOwner = this

        fetchMusicInfo()

        mediaControllerViewModel.play.observe(this, {
            isPlaying = it
            if(isPlaying) {
                mediaPlayer.start()
            } else {
                mediaPlayer.pause()
            }
        })
        seekbarThread.start()
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

                        mediaControllerViewModel.play.value = true
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