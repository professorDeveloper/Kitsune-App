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
import com.azamovhudstc.graphqlanilist.data.network.rest.jikan.JikanResponse
import com.azamovhudstc.graphqlanilist.data.repository.EpisodesRepositoryImpl
import com.azamovhudstc.graphqlanilist.utils.Result
import com.azamovhudstc.graphqlanilist.utils.logMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnimeWatchViewModel @Inject constructor(private val repositoryImpl: EpisodesRepositoryImpl) :
    ViewModel() {
    var lastPage = 0
    var totalChips = 0
    val episodeListLiveData: MutableLiveData<Result<JikanResponse>> = MutableLiveData()
    val imageList: MutableLiveData<Result<JikanResponse>> = MutableLiveData()
    fun loadEpisodesById(id: Int) {
        episodeListLiveData.postValue(Result.Loading)
        repositoryImpl.getEpisodesById(id)
        repositoryImpl.loadSuccessListener {
            lastPage = it.pagination.last_visible_page
            repositoryImpl.getEpisodesByIdPage(
                id,
                if (it.pagination.last_visible_page != 1) it.pagination.last_visible_page else it.pagination.last_visible_page
            )
        }
        repositoryImpl.loadErrorListener {
            episodeListLiveData.postValue(Result.Error(Exception(it.toString())))
        }
        repositoryImpl.loadPageSuccessListener {
            logMessage(it.toString())
            if (it.data.isEmpty() && !it.pagination.has_next_page) {
                repositoryImpl.getEpisodesByIdPage(id, lastPage - 1)
            } else {
                episodeListLiveData.postValue(Result.Success(it))
                logMessage(it.toString())
            }
        }
    }


    fun loadEpisodesImg(id: Int) {
        if (id!=0){
            repositoryImpl.getEpisodesById(id)
            repositoryImpl.loadSuccessListener {
                lastPage = it.pagination.last_visible_page
                repositoryImpl.getEpisodesByIdPage(
                    id,
                    if (it.pagination.last_visible_page != 1) it.pagination.last_visible_page else it.pagination.last_visible_page
                )
            }
            repositoryImpl.loadErrorListener {
                imageList.postValue(Result.Error(Exception(it.toString())))
            }
            repositoryImpl.loadPageSuccessListener {
                logMessage(it.toString())
                if (it.data.isEmpty() && !it.pagination.has_next_page) {
                    repositoryImpl.getEpisodesByIdPage(id, lastPage - 1)
                } else {
                    imageList.postValue(Result.Success(it))
                    logMessage(it.toString())
                }
            }

        }else{
            imageList.value =Result.Error()
        }
    }

}