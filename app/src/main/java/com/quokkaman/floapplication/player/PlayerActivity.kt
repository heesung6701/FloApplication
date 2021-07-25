package com.quokkaman.floapplication.player

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import androidx.lifecycle.ViewModelProvider
import com.quokkaman.floapplication.R
import com.quokkaman.floapplication.contoller.MediaPlayerServiceController
import com.quokkaman.floapplication.databinding.ActivityPlayerBinding
import com.quokkaman.floapplication.dto.SongDTO
import com.quokkaman.floapplication.lyric.LyricActivity
import com.quokkaman.floapplication.model.Song
import com.quokkaman.floapplication.player.viewmodel.MusicInfoViewModel
import com.quokkaman.floapplication.player.viewmodel.MusicLyricThumbViewModel
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
    private lateinit var musicLyricThumbViewModel: MusicLyricThumbViewModel
    private lateinit var seekbarViewModel: SeekbarViewModel
    private lateinit var mediaControllerViewModel: MediaControllerViewModel

    private var fetchedSong: Song? = null

    private var serviceController = MediaPlayerServiceController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        musicInfoViewModel = ViewModelProvider(this).get(MusicInfoViewModel::class.java)
        musicLyricThumbViewModel = ViewModelProvider(this).get(MusicLyricThumbViewModel::class.java)
        seekbarViewModel = ViewModelProvider(this).get(SeekbarViewModel::class.java).apply {
            mediaPlayerServiceController = serviceController
        }
        mediaControllerViewModel = ViewModelProvider(this).get(MediaControllerViewModel::class.java).apply {
            mediaPlayerServiceController = serviceController
        }

        binding.musicInfoViewModel = musicInfoViewModel
        binding.musicLyricViewModel = musicLyricThumbViewModel
        binding.seekbarViewModel = seekbarViewModel
        binding.mediaControllerViewModel = mediaControllerViewModel
        binding.lifecycleOwner = this

        fetchMusicInfo()
        binding.itemSeekbar.seekbar.setOnSeekBarChangeListener(seekbarViewModel.onSeekBarChangeListener)

        binding.itemLyric.root.setOnClickListener {
            val intent = Intent(this, LyricActivity::class.java)
            intent.putExtra(Song.INTENT_KEY, fetchedSong)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_up_enter, R.anim.nothing)
        }

        serviceController.msecConsumer = Consumer { msec ->
            musicLyricThumbViewModel.update(msec)
            seekbarViewModel.update(msec)
        }

        serviceController.durationConsumer = Consumer { duration ->
            val song = fetchedSong ?: return@Consumer
            mediaControllerViewModel.play()
            seekbarViewModel.durationLiveData.value = duration
            musicLyricThumbViewModel.updateLyricLine(song.lyricLineList, duration)
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