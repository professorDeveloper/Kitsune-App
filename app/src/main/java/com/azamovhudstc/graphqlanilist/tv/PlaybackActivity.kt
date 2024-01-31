/*
 *  Created by Azamov X ㋡ on 2/1/24, 12:23 AM
 *  Copyright (c) 2024 . All rights reserved.
 *  Last modified 2/1/24, 12:23 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/** Loads [PlaybackVideoFragment]. */
class PlaybackActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, PlaybackVideoFragment())
                .commit()
        }
    }
}