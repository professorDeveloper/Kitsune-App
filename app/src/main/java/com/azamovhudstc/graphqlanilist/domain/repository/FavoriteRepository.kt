package com.azamovhudstc.graphqlanilist.domain.repository

import androidx.paging.PagingData
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getGogoUrlFromAniListId(id: Int): Flow<String>
    fun getFavoriteAnimesFromAniList(
        userId: Int?
    ): Flow<PagingData<AniListMedia>>
}
