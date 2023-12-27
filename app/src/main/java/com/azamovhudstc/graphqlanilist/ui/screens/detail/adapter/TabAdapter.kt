/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.ui.screens.detail.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.data.model.ui_models.Media
import com.azamovhudstc.graphqlanilist.ui.screens.detail.pages.AnimeInfoPage
import com.azamovhudstc.graphqlanilist.ui.screens.detail.pages.AnimeWatchPage

class TabAdapter(var media: Media, var uiData: AniListMedia, fragmentManager: FragmentActivity) :
    FragmentStateAdapter(fragmentManager) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                AnimeInfoPage(media = media, aniListMedia = uiData)
            }
            else -> {
                val animeWatchPage  = AnimeWatchPage()

                val bundle =Bundle()
                bundle.putSerializable("data",uiData)
                animeWatchPage.arguments=bundle
                animeWatchPage
            }
        }
    }
}