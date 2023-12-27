/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.data.network.service

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.azamovhudstc.graphqlanilist.CharacterDataByIDQuery
import com.azamovhudstc.graphqlanilist.DetailFullDataQuery
import com.azamovhudstc.graphqlanilist.GetGenersByThumblainQuery
import com.azamovhudstc.graphqlanilist.SearchAnimeQuery
import com.azamovhudstc.graphqlanilist.type.MediaSort
import javax.inject.Inject

class AniListGraphQlClient @Inject constructor(
    private val apolloClient: ApolloClient
) : AniListSync {


    override suspend fun fetchSearchAniListData(
        query: String,
        page: Int,
        toMediaSort: List<MediaSort>
    ) = apolloClient.query(
        SearchAnimeQuery(
            Optional.presentIfNotNull(query),
            Optional.presentIfNotNull(page),
            Optional.present(toMediaSort)
        )
    ).execute()

    override suspend fun fetchFullDataById(id: Int) =
        apolloClient.query(DetailFullDataQuery(Optional.present(id))).execute()

    override suspend fun getImageByGenre(genre: String) = apolloClient.query(
        GetGenersByThumblainQuery(
            Optional.present(genre)
        )
    ).execute()

    override suspend fun getCharacterDataById(id: Int) = apolloClient.query(
        CharacterDataByIDQuery(
            Optional.present(id)
        )
    ).execute()


}