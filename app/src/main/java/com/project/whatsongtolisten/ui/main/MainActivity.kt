package com.project.whatsongtolisten.ui.main

import android.annotation.SuppressLint
import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.project.whatsongtolisten.R
import com.project.whatsongtolisten.adapter.MusicAdapter
import com.project.whatsongtolisten.common.data.Song
import com.project.whatsongtolisten.databinding.ActivityMainBinding
import com.project.whatsongtolisten.ui.base.BaseActivity
import com.project.whatsongtolisten.utils.LocalData
import org.koin.androidx.viewmodel.ext.android.viewModel

@Suppress("DEPRECATION")
class MainActivity : BaseActivity(), MusicAdapter.ItemMovieClickCallback {
    val vm: MainViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapterMusic: MusicAdapter
    var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        with(binding) {
            lifecycleOwner = this@MainActivity
            viewModel = vm
        }

        //listener from viewmodel when value has changed
        with(vm) {
            actionType.observe(this@MainActivity, Observer {
                when (it) {
                    MainViewModel.UPDATE_ADAPTER -> {
                        isLoadingVisible.value = false
                        adapterMusic.notifyDataSetChanged()
                    }
                }
            })
        }

        initView()
    }

    /**
     * init view
     */
    private fun initView() {
        setSupportActionBar(findViewById(R.id.toolbar))

        setMusicList()
        setButton()
    }

    /**
     * set binding click listener
     */
    private fun setButton() {
        binding.searchButton.setOnClickListener { view ->
            if (binding.etSearch.text.toString() != "") {
                disableFocusEdittext()
                vm.findSongs(binding.etSearch.text.toString())
            }
        }
        binding.btnPlayAndPause.setOnClickListener { view ->
            //Pause button when media player already exists, and start when exists
            if (mediaPlayer?.isPlaying!!) {
                vm.isPlaying.value = false

                try {
                    mediaPlayer?.pause()
                } catch (e: Exception) {}

                LocalData.removeSong()
                vm.actionType.value = MainViewModel.UPDATE_ADAPTER
            } else {
                vm.isPlaying.value = true

                try {
                    mediaPlayer?.start()
                } catch (e: Exception) {}

                LocalData.setNowPlayingMusic(vm.nowPlayingSong.value!!)
                vm.actionType.value = MainViewModel.UPDATE_ADAPTER
            }
        }
        binding.positionTime.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                /**
                 * set to position progress when user slide the seekbar
                 */
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mediaPlayer?.seekTo(progress * 1000)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}

                override fun onStopTrackingTouch(p0: SeekBar?) {}
            }
        )
    }

    /**
     * disable focus from edittext
     */
    private fun disableFocusEdittext() {
        binding.etSearch.isFocusableInTouchMode = false
        binding.etSearch.isFocusable = false
        binding.etSearch.isFocusableInTouchMode = true
        binding.etSearch.isFocusable = true
    }

    /**
     * set adapter from music list
     */
    private fun setMusicList() {
        val lLayout = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        val itemDecor = DividerItemDecoration(this, HORIZONTAL)

        adapterMusic = MusicAdapter(vm.resultList)
        binding.rvMusicList.layoutManager = lLayout
        binding.rvMusicList.setHasFixedSize(true)
        binding.rvMusicList.adapter = adapterMusic
        binding.rvMusicList.itemAnimator?.changeDuration = 0L
        binding.rvMusicList.addItemDecoration(itemDecor)
        adapterMusic.setItemClickCallback(this)
    }

    /**
     * interface adapter onclick
     */
    override fun onItemClick(position: Int) {
        vm.onFindNewArtis.value = false
        setSong(position)
    }

    /**
     * set song from choosed from list
     */
    private fun setSong(position: Int) {
        vm.nowPlayingSong.value = vm.resultList[position]
        vm.positionSong.value = position

        Glide.with(binding.itemNpBandPoster)
                .load(vm.resultList[position].artworkUrl100)
                .transform(CenterCrop())
                .placeholder(R.drawable.image)
                .into(binding.itemNpBandPoster)

        vm.setNowPlayingBar()
        playAudio(vm.nowPlayingSong.value)
    }

    /**
     * checking media player is already created or not
     */
    private fun playAudio(song: Song?) {
        if (vm.currentSong.value == null || vm.currentSong.value != song) {
            vm.currentSong.value = song
            LocalData.setNowPlayingMusic(song!!)

            try {
                if (mediaPlayer?.isPlaying!!) {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    vm.actionType.value = MainViewModel.UPDATE_ADAPTER
                }
            } catch (e: Exception) {}
        } else {
            if (mediaPlayer?.isPlaying!!) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                vm.actionType.value = MainViewModel.UPDATE_ADAPTER
            }
        }
        setMediaPlayer()
    }

    /**
     * set media player
     */
    private fun setMediaPlayer() {
        vm.isPlaying.value = true

        mediaPlayer = MediaPlayer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer?.setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build())
        } else {
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
        try {
            mediaPlayer?.setDataSource(vm.currentSong.value?.previewUrl)
            mediaPlayer?.prepare()

            mediaPlayer?.setOnCompletionListener(OnCompletionListener {
                vm.isPlaying.value = false
                LocalData.removeSong()

                if (vm.positionSong.value!! < vm.resultList.size - 1 && vm.onFindNewArtis.value == false) {
                    vm.positionSong.value = vm.positionSong.value?.plus(1)
                    setSong(vm.positionSong.value!!)
                }
                vm.actionType.value = MainViewModel.UPDATE_ADAPTER
            })

            mediaPlayer?.setOnPreparedListener {
                val songDuration = mediaPlayer?.duration!!.toInt()/1000

                binding.positionTime.max = songDuration
                binding.maxDuration.text = vm.parseTime(mediaPlayer?.duration!!)

                setMusicProgressTime()
                mediaPlayer?.start()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * create thread for time progress
     */
    private fun setMusicProgressTime() {
        Thread(Runnable {
            while (mediaPlayer != null) {
                try {
                    val msg = Message()
                    msg.what = mediaPlayer?.currentPosition!!
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: java.lang.Exception) {
                }
            }
        }).start()
    }

    /**
     * set text that will show in UI
     */
    @SuppressLint("HandlerLeak")
    var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val currentPosition = msg.what

            binding.positionTime.progress = currentPosition/1000

            val elapsedTime = vm.parseTime(currentPosition)
            binding.elapsedTime.text = elapsedTime
        }
    }
}