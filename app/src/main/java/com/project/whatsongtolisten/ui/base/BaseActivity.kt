package com.project.whatsongtolisten.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project.whatsongtolisten.WhatSongToListen

open class BaseActivity : AppCompatActivity() {

    private var mMyApp: WhatSongToListen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMyApp = this.applicationContext as WhatSongToListen
    }

    override fun onResume() {
        super.onResume()
        mMyApp?.setCurrentActivity(this)
    }

    override fun onPause() {
        clearReferences()
        super.onPause()
    }

    override fun onDestroy() {
        clearReferences()
        super.onDestroy()
    }

    private fun clearReferences() {
        val currActivity = mMyApp?.getCurrentActivity()
        if (this == currActivity) mMyApp?.setCurrentActivity(null)
    }
}