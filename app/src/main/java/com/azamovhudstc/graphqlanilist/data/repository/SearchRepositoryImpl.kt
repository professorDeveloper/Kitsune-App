/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.data.network.service.AniListGraphQlClient
import com.azamovhudstc.graphqlanilist.data.paging.SearchAniListPagingSource
import com.azamovhudstc.graphqlanilist.domain.repository.SearchRepository
import com.azamovhudstc.graphqlanilist.type.SortType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiClient: AniListGraphQlClient,
    private val ioDispatcher: CoroutineDispatcher
) : SearchRepository {
    override fun fetchSearchData(
        query: String,
        sortType: List<SortType>
    ): Flow<PagingData<AniListMedia>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = true, pageSize = NETWORK_PAGE_SIZE),
            pagingSourceFactory = { SearchAniListPagingSource(apiClient, query, sortType) }
        ).flow.flowOn(ioDispatcher)    }
    companion object {
        const val NETWORK_PAGE_SIZE = 50
    }
}