/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.data.network.service

import com.apollographql.apollo3.api.ApolloResponse
import com.azamovhudstc.graphqlanilist.CharacterDataByIDQuery
import com.azamovhudstc.graphqlanilist.DetailFullDataQuery
import com.azamovhudstc.graphqlanilist.GetGenersByThumblainQuery
import com.azamovhudstc.graphqlanilist.SearchAnimeQuery
import com.azamovhudstc.graphqlanilist.type.MediaSort
import com.azamovhudstc.graphqlanilist.utils.Apollo

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

    suspend fun getCharacterDataById(
        id: Int
    ):ApolloResponse<CharacterDataByIDQuery.Data>
}