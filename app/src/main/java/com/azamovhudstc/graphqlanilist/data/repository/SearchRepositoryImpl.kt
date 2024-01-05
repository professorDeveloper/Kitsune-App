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
import com.azamovhudstc.graphqlanilist.SearchAnimeQuery
import com.azamovhudstc.graphqlanilist.SearchByAnyQuery
import com.azamovhudstc.graphqlanilist.data.model.SearchResults
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.data.network.service.AniListGraphQlClient
import com.azamovhudstc.graphqlanilist.data.paging.SearchAniListPagingSource
import com.azamovhudstc.graphqlanilist.domain.repository.SearchRepository
import com.azamovhudstc.graphqlanilist.type.SortType
import com.azamovhudstc.graphqlanilist.utils.randomSelectFromList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
        ).flow.flowOn(ioDispatcher)
    }

    override fun randomAnimeList() =
        flow<Result<com.azamovhudstc.graphqlanilist.data.model.SearchResults>> {
            val genres =
                listOf(
                    "Action",
                    "Adventure",
                    "Comedy",
                    "Drama",
                    "Ecchi",
                    "Fantasy",
                    "Music",
                    "Romance"
                )
            val response = apiClient.search(
                query = "",
                page = 1,
                perPage = 50,
                toMediaSort = mutableListOf(
                    randomSelectFromList(genres)!!
                )
            )
            response.data.let {
                if (it != null) {
                    emit(
                        Result.success(
                            com.azamovhudstc.graphqlanilist.data.model.SearchResults(
                                type = "ANIME",
                                false,
                                false,
                                it.page?.pageInfo?.perPage ?: 50,
                                "",
                                "Score",
                                genres.subList(2, 4),
                                null,
                                null,
                                format = "TV",
                                hasNextPage = it.page?.pageInfo!!.hasNextPage!!,
                                results = it.page!!.media!! as ArrayList<SearchByAnyQuery.Medium?>,
                                page = it.page?.pageInfo?.currentPage!!,
                            )
                        )
                    )
                }

            }

        }

    override fun getSearch(r: SearchResults) = flow<SearchResults> {
        val response = apiClient.search(
            query = r.search!!,
            page = r.page,
            perPage = r.perPage!!.toInt(),
            toMediaSort = r.genres ?: listOf()
        )
        val genres =
            listOf(
                "Action",
                "Adventure",
                "Comedy",
                "Drama",
                "Ecchi",
                "Fantasy",
                "Music",
                "Romance"
            )
        response.data.let {
            if (it != null) {
                emit(
                    com.azamovhudstc.graphqlanilist.data.model.SearchResults(
                        type = "ANIME",
                        false,
                        false,
                        it.page?.pageInfo?.perPage ?: 50,
                        "",
                        "Score",
                        genres.subList(2, 4),
                        null,
                        null,
                        format = "TV",
                        hasNextPage = it.page?.pageInfo!!.hasNextPage!!,
                        results = it.page!!.media!! as ArrayList<SearchByAnyQuery.Medium?>,
                        page = it.page?.pageInfo?.currentPage!!,
                    )
                )

            }
        }
    }

    override fun fetchSearchAniListData(
        query: String,
        page: Int
    ) = flow<SearchResults> {
        val response = apiClient.fetchSearchAniListData(query, page, listOf())
        if (response.data != null) {
            response.data.let {
                emit(
                    com.azamovhudstc.graphqlanilist.data.model.SearchResults(
                        type = "ANIME",
                        false,
                        false,
                        it!!.page?.pageInfo?.perPage ?: 50,
                        "",
                        "Score",
                        null,
                        null,
                        hasNextPage = false,
                        format = "TV",
                        results = it.convert(),
                    )
                )
            }
        }
    }

    fun SearchAnimeQuery.Data.convert(): ArrayList<SearchByAnyQuery.Medium?>? {
        val newList = ArrayList<SearchByAnyQuery.Medium?>()

        this.page?.media!!.onEach {
            if (it != null) {
                newList.add(
                    SearchByAnyQuery.Medium(
                        it.__typename,
                        it.homeMedia
                    )
                )
            }
        }
        return newList
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 50
    }
}