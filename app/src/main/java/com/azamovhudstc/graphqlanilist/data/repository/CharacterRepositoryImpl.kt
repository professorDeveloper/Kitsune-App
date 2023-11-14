package com.azamovhudstc.graphqlanilist.data.repository

import com.azamovhudstc.graphqlanilist.CharacterDataByIDQuery
import com.azamovhudstc.graphqlanilist.data.network.service.AniListGraphQlClient
import com.azamovhudstc.graphqlanilist.domain.repository.CharacterRepository
import com.azamovhudstc.graphqlanilist.utils.convert
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(private val apolloClient: AniListGraphQlClient) :
    CharacterRepository {
    override fun getCharacterById(id: Int) = flow {
        val response = apolloClient.getCharacterDataById(id)
        if (response.data != null) {
            emit(Result.success(response.data!!.convert()))
        }
    }


}