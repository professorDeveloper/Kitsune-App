package com.azamovhudstc.graphqlanilist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.data.repository.SearchRepositoryImpl
import com.azamovhudstc.graphqlanilist.domain.repository.SearchRepository
import com.azamovhudstc.graphqlanilist.type.SortType
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

    private val _searchList = MutableStateFlow<PagingData<AniListMedia>>(PagingData.empty())
    val searchList: StateFlow<PagingData<AniListMedia>> = _searchList.asStateFlow()
    private val sortTypes = mutableListOf<SortType>()
    private var lastSearchQuery = "All"

    private val searchQueryChannel = Channel<SearchQuery>(Channel.CONFLATED)
    private val searchFlow = searchQueryChannel
        .receiveAsFlow()
        .debounce(500)
        .filter { it.query.length >= 2 }
        .flatMapLatest { searchRepository.fetchSearchData(it.query, sortTypes) }
        .cachedIn(viewModelScope)
        .flowOn(ioDispatcher)

    init {
        viewModelScope.launch {
            searchFlow.collect {
                _searchList.value = it
            }
        }
    }

    fun onSearchQueryChanged(query: String="",type:MutableList<SortType>?=null) {
        lastSearchQuery = query.trim()
        searchQueryChannel.trySend(SearchQuery(lastSearchQuery, type?:sortTypes)).isSuccess
    }

    fun toggleSortType(sortType: SortType) {
        if (sortTypes.contains(sortType)) {
            sortTypes.remove(sortType)
        } else {
            sortTypes.add(sortType)
        }
        onSearchQueryChanged(lastSearchQuery)
    }
}

data class SearchQuery(
    val query: String,
    val sortTypes: MutableList<SortType>
)