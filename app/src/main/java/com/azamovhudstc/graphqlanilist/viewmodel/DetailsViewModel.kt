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
import com.azamovhudstc.graphqlanilist.data.model.ui_models.Media
import com.azamovhudstc.graphqlanilist.data.repository.DetailRepositoryImpl
import com.azamovhudstc.graphqlanilist.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repositoryImpl: DetailRepositoryImpl
) : ViewModel() {
     val responseData = MutableLiveData<Result<Media>>()
    fun loadDataById(id: Int) {
        responseData.postValue(com.azamovhudstc.graphqlanilist.utils.Result.Loading)

        repositoryImpl.getFullDataByID(id).onEach {
            responseData.postValue(com.azamovhudstc.graphqlanilist.utils.Result.Success(it))
        }.launchIn(viewModelScope)

    }
}

