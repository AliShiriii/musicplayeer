package com.example.musicplayer.application

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication: Application() {

    //BaseApplication
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        MultiDex.install(this)

    }
}