package com.azamovhudstc.graphqlanilist.data.local

interface BaseClient {
    var animeService: BaseService
    val parser: BaseParser

    suspend fun <T> fetchEpisodeMediaUrl(
        header: Map<String, String>,
        episodeUrl: String,
        extra: List<Any?> = emptyList()
    ): T

    suspend fun <T> fetchEpisodeList(
        episodeUrl: String,
        extra: List<Any?> = emptyList()
    ): T

    suspend fun <T> getEpisodeTitles(id: Int): T
}
