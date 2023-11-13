package com.azamovhudstc.graphqlanilist.data.repository

import com.azamovhudstc.graphqlanilist.data.mapper.convert
import com.azamovhudstc.graphqlanilist.data.model.ui_models.Media
import com.azamovhudstc.graphqlanilist.data.model.ui_models.Pages
import com.azamovhudstc.graphqlanilist.data.network.service.AniListGraphQlClient
import com.azamovhudstc.graphqlanilist.domain.repository.DetailRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DetailRepositoryImpl @Inject constructor(
    private val apiClient: AniListGraphQlClient,
    private val ioDispatcher: CoroutineDispatcher
) : DetailRepository {
    override  fun getFullDataByID(mediaId: Int) = flow<Media> {
        val response = apiClient.fetchFullDataById(id = mediaId)
        val detailFullData = response.data?.convert()
        emit(detailFullData!!)
    }.flowOn(ioDispatcher)

    override fun getImagesByGenre(genre: String)=flow<Pages> {
        val response =apiClient.getImageByGenre(genre)
        val  responseParser =response.data?.convert()

        emit(responseParser!!)

    }

}