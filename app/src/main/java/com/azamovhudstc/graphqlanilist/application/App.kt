/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.application

import android.app.Application
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.azamovhudstc.graphqlanilist.utils.Constants
import com.azamovhudstc.graphqlanilist.utils.initializeNetwork
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
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
        initializeNetwork(this)

    }
    private fun disableNightMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}