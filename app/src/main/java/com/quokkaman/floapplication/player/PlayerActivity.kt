package com.quokkaman.floapplication.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.quokkaman.floapplication.databinding.ActivityPlayerBinding
import com.quokkaman.floapplication.dto.SongDTO
import com.quokkaman.floapplication.lyric.LyricActivity
import com.quokkaman.floapplication.model.Song
import com.quokkaman.floapplication.player.viewmodel.MusicInfoViewModel
import com.quokkaman.floapplication.player.viewmodel.MusicLyricViewModel
import com.quokkaman.floapplication.repository.SongRepository
import com.quokkaman.floapplication.service.MediaPlayerService
import com.quokkaman.floapplication.viewmodel.MediaControllerViewModel
import com.quokkaman.floapplication.viewmodel.SeekbarViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.lang.Exception


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

    private var draggingSeekbar = false
    private var fetchedSong: Song? = null

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_WHAT_MILLISECOND -> {
                    val playMillisecond: Int = msg.data.getInt(KEY_MSEC)
                    Log.d("HSSS2", "msec" + playMillisecond)
                    musicLyricViewModel.update(playMillisecond)
                    if (!draggingSeekbar) {
                        seekbarViewModel.update(playMillisecond)
                    }
                }
            }
        }
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val intent = p1?:return
            when(intent.action) {
                MediaPlayerService.EVENT_MSEC -> {
                    val msec = intent.getIntExtra(MediaPlayerService.MESSAGE_MSEC, 0)
                    Log.d("HSSS", "msec" + msec)
                    handler.sendMessage(Message().apply {
                        what = MESSAGE_WHAT_MILLISECOND
                        data.putInt(KEY_MSEC, msec)
                    })
                }
                MediaPlayerService.EVENT_SET_SOURCE -> {
                    val duration = intent.getIntExtra(MediaPlayerService.MESSAGE_DURATION, 0)
                    Log.d("HSSS", "duration" + duration)
                    mediaControllerViewModel.play()
                    seekbarViewModel.durationLiveData.value = duration
                    fetchedSong?.let {
                        musicLyricViewModel.updateLyricLine(it.lyricLineList, duration)
                    }
                }
                else ->{
                    throw Exception(intent.action+"")
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
            ViewModelProvider(this).get(MediaControllerViewModel::class.java)

        binding.musicInfoViewModel = musicInfoViewModel
        binding.musicLyricViewModel = musicLyricViewModel
        binding.seekbarViewModel = seekbarViewModel
        binding.mediaControllerViewModel = mediaControllerViewModel
        binding.lifecycleOwner = this

        fetchMusicInfo()
        mediaControllerViewModel.playingLiveData.observe(this, {
            if(it) {
                startService(Intent(applicationContext, MediaPlayerService::class.java).apply {
                    action = MediaPlayerService.ACTION_PLAY
                })
            } else {
                startService(Intent(applicationContext, MediaPlayerService::class.java).apply {
                    action = MediaPlayerService.ACTION_PAUSE
                })
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
                startService(Intent(applicationContext, MediaPlayerService::class.java).apply {
                    action = MediaPlayerService.ACTION_SEEKTO
                    putExtra(MediaPlayerService.MESSAGE_MSEC, seekBar.progress)
                })
                mediaControllerViewModel.play()
            }

        })

        binding.itemLyric.root.setOnClickListener {
            val intent = Intent(this, LyricActivity::class.java)
            startActivity(intent)
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

                        startService(
                            Intent(
                                applicationContext,
                                MediaPlayerService::class.java
                            ).apply {
                                action = MediaPlayerService.ACTION_SET
                                putExtra("media", t.file)
                            })
                    }

                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                    }
                })
        )
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver, IntentFilter().apply {
                addAction(MediaPlayerService.EVENT_MSEC)
                addAction(MediaPlayerService.EVENT_SET_SOURCE)
            }
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        startService(Intent(applicationContext, MediaPlayerService::class.java).apply {
            action = MediaPlayerService.ACTION_RELEASE
        })
        disposable.clear()
    }
}