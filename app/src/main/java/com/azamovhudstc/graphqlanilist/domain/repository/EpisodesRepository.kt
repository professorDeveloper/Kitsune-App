package com.azamovhudstc.graphqlanilist.domain.repository

import com.azamovhudstc.graphqlanilist.data.network.rest.jikan.JikanResponse
import kotlinx.coroutines.flow.Flow

interface EpisodesRepository {
    fun getEpisodesByIdPage(id:Int,page:Int)
    fun getEpisodesById(id:Int,)
}