/*
 *  Created by Azamov X ㋡ on 2/1/24, 12:54 AM
 *  Copyright (c) 2024 . All rights reserved.
 *  Last modified 2/1/24, 12:22 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.azamovhudstc.graphqlanilist.R

/**
 * Details activity class that loads [VideoDetailsFragment] class.
 */
class DetailsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.details_fragment, VideoDetailsFragment())
                .commitNow()
        }
    }

    companion object {
        const val SHARED_ELEMENT_NAME = "hero"
        const val MOVIE = "Movie"
    }
}