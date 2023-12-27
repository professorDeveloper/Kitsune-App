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
import com.azamovhudstc.graphqlanilist.CharacterDataByIDQuery
import com.azamovhudstc.graphqlanilist.data.model.ui_models.CharacterMedia
import com.azamovhudstc.graphqlanilist.domain.repository.CharacterRepository
import com.azamovhudstc.graphqlanilist.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CharacterViewModel @Inject constructor(private val characterRepositoryImpl: CharacterRepository) :
    ViewModel() {
        val characterData:MutableLiveData<Result<CharacterMedia>> =MutableLiveData()

     fun loadData(id:Int){
         characterData.postValue(Result.Loading)
         characterRepositoryImpl.getCharacterById(id).onEach {
             it.onSuccess {
                 characterData.postValue(Result.Success(it))
             }

             it.onFailure {
                 characterData.postValue(Result.Error(it))
             }
         }.launchIn(viewModelScope)
     }
}