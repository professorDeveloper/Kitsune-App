/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.azamovhudstc.graphqlanilist.data.model.SearchResults
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.data.repository.SearchRepositoryImpl
import com.azamovhudstc.graphqlanilist.type.SortType
import com.azamovhudstc.graphqlanilist.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepositoryImpl,
    ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    val result: MutableLiveData<com.azamovhudstc.graphqlanilist.utils.Resource<SearchResults>> =
        MutableLiveData()
    val searchResult: MutableLiveData<com.azamovhudstc.graphqlanilist.utils.Resource<SearchResults>> =
        MutableLiveData()
    var searched = false
    var notSet = true
    lateinit var searchResults: SearchResults
    private val _searchList = MutableStateFlow<PagingData<AniListMedia>>(PagingData.empty())
    private val sortTypes = mutableListOf<SortType>()
    private var lastSearchQuery = "All"

    fun onSearchQueryChanged(query: String = "") {
        searchResult.postValue(Resource.Loading)
        searchRepository.fetchSearchAniListData(query,1).onEach {
            searchResult.postValue(com.azamovhudstc.graphqlanilist.utils.Resource.Success(it))
        }.launchIn(viewModelScope)
    }

    fun toggleSortType(sortType: SortType) {
        if (sortTypes.contains(sortType)) {
            sortTypes.remove(sortType)
        } else {
            sortTypes.add(sortType)
        }
        onSearchQueryChanged(lastSearchQuery)
    }

    fun loadAllAnimeList() {
        result.postValue(com.azamovhudstc.graphqlanilist.utils.Resource.Loading)
        searchRepository.randomAnimeList().onEach {

            it.onFailure {
                result.postValue(com.azamovhudstc.graphqlanilist.utils.Resource.Error(it))
            }

            it.onSuccess {

                result.postValue(com.azamovhudstc.graphqlanilist.utils.Resource.Success(it))
            }
        }.launchIn(viewModelScope)
    }

    fun loadNextPage(r: SearchResults) {
        val data = r.copy(page = r.page + 1)
        result.postValue(com.azamovhudstc.graphqlanilist.utils.Resource.Loading)
        searchRepository.getSearch(data).onEach {

            println(it.page)
            println(r.page)
            result.postValue(com.azamovhudstc.graphqlanilist.utils.Resource.Success(it))
        }.launchIn(viewModelScope)

    }
}

data class SearchQuery(
    val query: String,
    val sortTypes: MutableList<SortType>
)