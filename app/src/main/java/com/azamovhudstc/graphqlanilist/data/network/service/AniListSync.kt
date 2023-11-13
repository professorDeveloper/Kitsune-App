package com.azamovhudstc.graphqlanilist.data.network.service

import com.apollographql.apollo3.api.ApolloResponse
import com.azamovhudstc.graphqlanilist.DetailFullDataQuery
import com.azamovhudstc.graphqlanilist.GetGenersByThumblainQuery
import com.azamovhudstc.graphqlanilist.SearchAnimeQuery
import com.azamovhudstc.graphqlanilist.type.MediaSort

interface AniListSync {
    suspend fun fetchSearchAniListData(
        query: String,
        page: Int,
        toMediaSort: List<MediaSort>
    ): ApolloResponse<SearchAnimeQuery.Data>

    suspend fun fetchFullDataById(
        id: Int,
    ): ApolloResponse<DetailFullDataQuery.Data>
    suspend fun getImageByGenre(
        genre: String,
    ): ApolloResponse<GetGenersByThumblainQuery.Data>

}