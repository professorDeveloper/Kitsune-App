package com.azamovhudstc.graphqlanilist.application

import android.app.Application
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.azamovhudstc.graphqlanilist.utils.Constants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App:Application() {
    companion object {
        lateinit var instance: App

    }

    override fun onCreate() {
        super.onCreate()
        instance=this
        disableNightMode()

    }
    private fun disableNightMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}