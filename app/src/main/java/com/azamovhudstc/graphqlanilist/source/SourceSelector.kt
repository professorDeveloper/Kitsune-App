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

class SourceSelector(context: Context) {
    val sourceMap: Map<String, AnimeSource> = mapOf(
        "allanime" to AllAnimeSource(),
    )

    fun getSelectedSource(selectedSource: String): AnimeSource {
        if (selectedSource in sourceMap.keys) {
            return sourceMap[selectedSource]!!
        }
        return sourceMap["yugen"]!!
    }
}