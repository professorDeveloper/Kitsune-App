/*
 *  Created by Azamov X ㋡ on 1/20/24, 5:43 PM
 *  Copyright (c) 2024 . All rights reserved.
 *  Last modified 1/20/24, 5:43 PM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azamovhudstc.graphqlanilist.data.model.SourceModel
import com.azamovhudstc.graphqlanilist.source.AnimeSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SourceViewModel:ViewModel() {
    var response:MutableLiveData<List<SourceModel>> = MutableLiveData()
    fun search(query:String,source:AnimeSource) = callbackFlow<ArrayList<SourceModel>>{
        trySend( source.searchAnime(query))
       awaitClose()
    }.flowOn(Dispatchers.IO)
}