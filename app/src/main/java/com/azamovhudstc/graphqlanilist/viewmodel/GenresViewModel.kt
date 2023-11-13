package com.azamovhudstc.graphqlanilist.viewmodel

import androidx.lifecycle.ViewModel
import com.azamovhudstc.graphqlanilist.data.model.ui_models.GenreByImage
import com.azamovhudstc.graphqlanilist.data.repository.DetailRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class GenresViewModel @Inject constructor(
    private val repositoryImpl: DetailRepositoryImpl
) : ViewModel() {
    var genres: MutableMap<String, String>? = null
    var done = false
    var doneListener: (() -> Unit)? = null
    suspend fun loadGenres(genre: ArrayList<String>, listener: (Pair<String, String>) -> Unit) {
        if (genres == null) {
            genres = mutableMapOf()
            getGenres(genre) {
                genres!![it.first] = it.second
                listener.invoke(it)
                if (genres!!.size == genre.size) {
                    done = true
                    doneListener?.invoke()
                }
            }
        }
    }


    suspend fun getGenres(genres: ArrayList<String>, listener: ((Pair<String, String>) -> Unit)) {

        }

    }