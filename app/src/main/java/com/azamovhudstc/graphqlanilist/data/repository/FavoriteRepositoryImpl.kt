package com.azamovhudstc.graphqlanilist.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.data.network.service.AniListGraphQlClient
import com.azamovhudstc.graphqlanilist.data.paging.FavoritesPagingSource
import com.azamovhudstc.graphqlanilist.data.service.GogoAnimeApiClient
import com.azamovhudstc.graphqlanilist.domain.repository.FavoriteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ExperimentalCoroutinesApi
class FavoriteRepositoryImpl @Inject constructor(
    private val aniListGraphQlClient: AniListGraphQlClient,
    private val apiClient: GogoAnimeApiClient,
    private val ioDispatcher: CoroutineDispatcher
) : FavoriteRepository {

    override fun getGogoUrlFromAniListId(id: Int) = flow<String> {
        emit(apiClient.getGogoUrlFromAniListId(id).pages?.getGogoUrl().orEmpty())
    }.flowOn(ioDispatcher)

    override fun getFavoriteAnimesFromAniList(
        userId: Int?
    ): Flow<PagingData<AniListMedia>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = true, pageSize = NETWORK_PAGE_SIZE),
            pagingSourceFactory = { FavoritesPagingSource(aniListGraphQlClient, userId) }
        ).flow.flowOn(ioDispatcher)
    }
    companion object {
        const val NETWORK_PAGE_SIZE = 50
    }
}
