/*
 *  Created by Azamov X ㋡ on 2/1/24, 12:23 AM
 *  Copyright (c) 2024 . All rights reserved.
 *  Last modified 2/1/24, 12:22 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.tv

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BackgroundManager
import com.azamovhudstc.graphqlanilist.R
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint

/**
 * Loads [MainFragment].
 */
@AndroidEntryPoint
class MainTvActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_main_tv)

        val backgroundManager = BackgroundManager.getInstance(this)
        backgroundManager.attach(this.window)

        backgroundManager.color = ContextCompat.getColor(this, R.color.episode_bg)
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_browse_fragment, MainFragment())
            .commitNow()
    }
}