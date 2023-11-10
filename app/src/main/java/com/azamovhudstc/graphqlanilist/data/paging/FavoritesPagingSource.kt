package com.azamovhudstc.graphqlanilist.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.data.network.service.AniListGraphQlClient
import com.azamovhudstc.graphqlanilist.utils.convert

class FavoritesPagingSource(
    private val apiClient: AniListGraphQlClient,
    private val userId: Int?
) : PagingSource<Int, AniListMedia>() {
    override fun getRefreshKey(state: PagingState<Int, AniListMedia>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AniListMedia> {
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            val response = apiClient.getFavoriteAnimes(userId, page)
            val listOfAniListMedia = response.convert()
            LoadResult.Page(
                data = listOfAniListMedia,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (listOfAniListMedia.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        const val STARTING_PAGE_INDEX = 1
    }
}
