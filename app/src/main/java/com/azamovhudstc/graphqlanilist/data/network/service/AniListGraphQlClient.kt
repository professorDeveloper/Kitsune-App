package com.azamovhudstc.graphqlanilist.data.network.service

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.azamovhudstc.graphqlanilist.FavoritesAnimeQuery
import com.azamovhudstc.graphqlanilist.SaveMediaMutation
import com.azamovhudstc.graphqlanilist.SearchAnimeQuery
import com.azamovhudstc.graphqlanilist.type.MediaListStatus
import com.azamovhudstc.graphqlanilist.type.MediaSort
import javax.inject.Inject

class AniListGraphQlClient @Inject constructor(
    private val apolloClient: ApolloClient
) : AniListSync {
    override suspend fun getFavoriteAnimes(
        userId: Int?,
        page: Int?
    ) = apolloClient.query(
        FavoritesAnimeQuery(
            Optional.Present(userId),
            Optional.Present(page)
        )
    ).execute()

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
    override suspend fun markAnimeStatus(
        mediaId: Int,
        status: MediaListStatus
    ) = apolloClient.mutation(
        SaveMediaMutation(mediaId, status)
    ).execute()

}