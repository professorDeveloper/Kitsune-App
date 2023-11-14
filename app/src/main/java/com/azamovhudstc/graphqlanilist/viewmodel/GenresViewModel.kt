package com.azamovhudstc.graphqlanilist.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azamovhudstc.graphqlanilist.GetGenersByThumblainQuery
import com.azamovhudstc.graphqlanilist.data.repository.DetailRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class GenresViewModel @Inject constructor(
    private val repositoryImpl: DetailRepositoryImpl
) : ViewModel() {
    var genres: MutableLiveData<List<GetGenersByThumblainQuery.Medium?>> = MutableLiveData()
    var done = false
     fun loadGenres() {
        repositoryImpl.getImagesByGenre("MYSTERY").onEach {
            genres.postValue(it.media?.toMutableList()!!)
        }.launchIn(viewModelScope)
    }


}