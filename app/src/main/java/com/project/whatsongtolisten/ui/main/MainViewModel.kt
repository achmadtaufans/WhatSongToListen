package com.project.whatsongtolisten.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.whatsongtolisten.common.api.UseCaseResult
import com.project.whatsongtolisten.common.data.Song
import com.project.whatsongtolisten.common.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val musicRepository: MusicRepository): ViewModel() {
    val actionType: MutableLiveData<Int> = MutableLiveData<Int>().apply { 0 }
    val resultList: ArrayList<Song> = arrayListOf()
    val currentSong: MutableLiveData<Song> = MutableLiveData()
    val nowPlayingSong: MutableLiveData<Song> = MutableLiveData()
    val nowPlayingTitle: MutableLiveData<String> = MutableLiveData<String>().apply { "" }
    val nowPlayingArtist: MutableLiveData<String> = MutableLiveData<String>().apply { "" }
    val isPlaying: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { false }
    val positionSong: MutableLiveData<Int> = MutableLiveData<Int>().apply { 0 }
    val isLoadingVisible: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { false }
    val onFindNewArtis: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { false }
    val onFindArtistFound: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { true }

    companion object {
        const val UPDATE_ADAPTER = 1
    }

    init {
        onFindArtistFound.value = true
    }

    fun findSongs(artist: String) {
        resultList.clear()
        onFindNewArtis.value = true
        isLoadingVisible.value = true
        GlobalScope.launch {
            val result = withContext(Dispatchers.IO) { musicRepository.getSongs(artist) }

            when (result) {
                is UseCaseResult.Success ->
                    if (result.data.results != null) {
                        for (item in result.data.results!!.indices) {
                            if (!result.data.results!![item].previewUrl.isNullOrEmpty()) {
                                resultList.add(result.data.results!![item])
                            }
                        }

                        onFindArtistFound.postValue(resultList.isNotEmpty())

                        actionType.postValue(UPDATE_ADAPTER)
                    }
                is UseCaseResult.Error -> {}
            }
        }
    }

    fun setNowPlayingBar() {
        nowPlayingTitle.value = nowPlayingSong.value?.trackName
        nowPlayingArtist.value = nowPlayingSong.value?.artistName
        actionType.postValue(UPDATE_ADAPTER)
    }

    fun parseTime(time: Int): String {
        var timeText = ""
        val min = time / 1000 / 60
        val sec = time / 1000 % 60

        timeText = "$min:"
        if (sec < 10) timeText += "0"
        timeText += "$sec"

        return timeText
    }
}