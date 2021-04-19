package com.project.whatsongtolisten.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.project.whatsongtolisten.WhatSongToListen
import com.project.whatsongtolisten.common.data.Song

object LocalData {
    const val SP_KEY = "WhatSongToListen"
    const val NOW_PLAYING_SONG = "now_playing_song"

    lateinit var sharedPreferences: SharedPreferences

    var gson: Gson = Gson()

    fun setNowPlayingMusic(song: Song) {
        val json: String = gson.toJson(song)
        sharedPreferences = WhatSongToListen.applicationContext().getSharedPreferences(SP_KEY, Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        editor.putString(NOW_PLAYING_SONG, json)
        editor.apply()
    }

    fun getNowPlayingMusic(): Song? {
        sharedPreferences = WhatSongToListen.applicationContext().getSharedPreferences(SP_KEY, Context.MODE_PRIVATE)

        val json = sharedPreferences.getString(NOW_PLAYING_SONG, null)
        var song: Song? = null

        if (json != null) {
            song = gson.fromJson(json, Song::class.java)
        }

        return song
    }

    fun removeSong() {
        val settings = WhatSongToListen.applicationContext().getSharedPreferences(SP_KEY, Context.MODE_PRIVATE)

        settings.edit().remove(NOW_PLAYING_SONG).apply()
    }
}