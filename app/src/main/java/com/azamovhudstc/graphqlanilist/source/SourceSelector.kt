/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.source

import android.content.Context
import com.azamovhudstc.graphqlanilist.source.source_imp.AllAnimeSource
import com.azamovhudstc.graphqlanilist.source.source_imp.AniWaveSource
import com.azamovhudstc.graphqlanilist.source.source_imp.AniWorldSource
import com.azamovhudstc.graphqlanilist.source.source_imp.YugenSource
import com.azamovhudstc.graphqlanilist.source.source_imp.ZoroSource

class SourceSelector(context: Context) {
    val sourceMap: Map<String, AnimeSource> = mapOf(
        "yugen" to YugenSource(),
        "allanime" to ZoroSource(),
        "aniworld" to AniWorldSource(),

        )

    fun getSelectedSource(selectedSource: String): AnimeSource {
        if (selectedSource in sourceMap.keys) {
            return sourceMap[selectedSource]!!
        }
        return sourceMap["yugen"]!!
    }
}