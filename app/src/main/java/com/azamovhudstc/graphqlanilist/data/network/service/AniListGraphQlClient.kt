/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.data.network.service

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.azamovhudstc.graphqlanilist.*
import com.azamovhudstc.graphqlanilist.type.MediaSort
import javax.inject.Inject

class AniListGraphQlClient @Inject constructor(
    private val apolloClient: ApolloClient
) : AniListSync {


    suspend fun search(
        query: String = "",
        page: Int = 1,
        perPage: Int = 50,
        toMediaSort: List<String>
    ) = apolloClient.query(
        SearchByAnyQuery(
            Optional.present(perPage),
            page = Optional.present(page),
            sort = Optional.present(
                listOf<MediaSort>(
                    MediaSort.POPULARITY_DESC,
                    MediaSort.SCORE_DESC
                )
            ),
        )
    ).execute()

    override suspend fun fetchSearchAniListData(
        query: String,
        page: Int,
        toMediaSort: List<String>
    ): ApolloResponse<SearchAnimeQuery.Data> {
        return apolloClient.query(SearchAnimeQuery(Optional.present(query), Optional.present(1)))
            .execute()
    }


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