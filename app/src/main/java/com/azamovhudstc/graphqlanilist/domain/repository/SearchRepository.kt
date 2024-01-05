/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.domain.repository

import android.app.appsearch.SearchResults
import androidx.paging.PagingData
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.type.SortType
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun fetchSearchData(query: String, sortType: List<SortType>): Flow<PagingData<AniListMedia>>
    fun randomAnimeList(): Flow<Result<com.azamovhudstc.graphqlanilist.data.model.SearchResults>>
    fun getSearch(r: com.azamovhudstc.graphqlanilist.data.model.SearchResults): Flow<com.azamovhudstc.graphqlanilist.data.model.SearchResults>
    fun fetchSearchAniListData(
        query: String = "",
        page: Int,
    ): Flow<com.azamovhudstc.graphqlanilist.data.model.SearchResults>
}