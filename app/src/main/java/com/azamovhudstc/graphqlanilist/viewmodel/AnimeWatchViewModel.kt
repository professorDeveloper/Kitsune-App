package com.azamovhudstc.graphqlanilist.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azamovhudstc.graphqlanilist.data.network.rest.jikan.JikanResponse
import com.azamovhudstc.graphqlanilist.data.repository.EpisodesRepositoryImpl
import com.azamovhudstc.graphqlanilist.utils.Result
import com.azamovhudstc.graphqlanilist.utils.logMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnimeWatchViewModel @Inject constructor(private val repositoryImpl: EpisodesRepositoryImpl) :
    ViewModel() {
    var lastPage=0
    val episodeListLiveData: MutableLiveData<Result<JikanResponse>> = MutableLiveData()
    fun loadEpisodesById(id: Int) {
        episodeListLiveData.postValue(Result.Loading)
        repositoryImpl.getEpisodesById(id)
        repositoryImpl.loadSuccessListener {
            lastPage= it.pagination.last_visible_page
            repositoryImpl.getEpisodesByIdPage(
                id,
                if (it.pagination.last_visible_page != 1) it.pagination.last_visible_page else it.pagination.last_visible_page
            )
        }
        repositoryImpl.loadPageSuccessListener {
            if (it.data.isEmpty() && !it.pagination.has_next_page) {
                repositoryImpl.getEpisodesByIdPage(id, lastPage - 1)
            } else {
                episodeListLiveData.postValue(Result.Success(it))
                logMessage(it.toString())
            }
        }
    }
}