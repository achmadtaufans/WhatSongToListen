package com.project.whatsongtolisten

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import initDependencyInjection

class WhatSongToListen : MultiDexApplication() {

    init {
        instance = this
    }

    override fun onCreate() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        initDependencyInjection(this)
        super.onCreate()
    }

    private var mCurrentActivity: Activity? = null
    fun getCurrentActivity(): Activity? {
        return mCurrentActivity
    }

    fun setCurrentActivity(mCurrentActivity: Activity?) {
        this.mCurrentActivity = mCurrentActivity
    }

    companion object {
        private var instance: WhatSongToListen? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}