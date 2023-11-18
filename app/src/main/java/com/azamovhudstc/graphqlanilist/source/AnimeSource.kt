package com.azamovhudstc.graphqlanilist.source

import com.azamovhudstc.graphqlanilist.data.model.AnimeStreamLink
import com.azamovhudstc.graphqlanilist.source.source_imp.AllAnimeSource


interface AnimeSource {
    suspend fun getEpisodeInfos(showId: String): List<AllAnimeSource.EpisodeInfo>?
    suspend fun trendingAnime(): ArrayList<Pair<String, String>>
    suspend fun searchAnime(text: String): ArrayList<Pair<String, String>>
    suspend fun animeDetails(id: String): Map<String, Map<String, String>>
    suspend fun streamLink(
        animeUrl: String,
        animeEpCode: String,
        extras: List<String>? = null
    ): AnimeStreamLink

}

