package com.azamovhudstc.graphqlanilist.data.network.service

import com.apollographql.apollo3.api.ApolloResponse
import com.azamovhudstc.graphqlanilist.FavoritesAnimeQuery
import com.azamovhudstc.graphqlanilist.SaveMediaMutation
import com.azamovhudstc.graphqlanilist.SearchAnimeQuery
import com.azamovhudstc.graphqlanilist.type.MediaListStatus
import com.azamovhudstc.graphqlanilist.type.MediaSort

interface AniListSync {
    suspend fun fetchSearchAniListData(
        query: String,
        page: Int,
        toMediaSort: List<MediaSort>
    ): ApolloResponse<SearchAnimeQuery.Data>
    suspend fun getFavoriteAnimes(
        userId: Int?,
        page: Int?
    ): ApolloResponse<FavoritesAnimeQuery.Data>
    suspend fun markAnimeStatus(
        mediaId: Int,
        status: MediaListStatus
    ): ApolloResponse<SaveMediaMutation.Data>

}