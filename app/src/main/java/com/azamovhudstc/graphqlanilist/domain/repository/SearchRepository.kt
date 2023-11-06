package com.azamovhudstc.graphqlanilist.domain.repository

import androidx.paging.PagingData
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.type.SortType
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun fetchSearchData(query:String,sortType:List<SortType>,):Flow<PagingData<AniListMedia>>
}