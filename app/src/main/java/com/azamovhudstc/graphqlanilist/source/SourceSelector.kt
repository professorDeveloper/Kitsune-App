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