package com.azamovhudstc.graphqlanilist.domain.repository

import com.azamovhudstc.graphqlanilist.CharacterDataByIDQuery
import com.azamovhudstc.graphqlanilist.data.model.ui_models.CharacterMedia
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getCharacterById(id: Int): Flow<Result<CharacterMedia>>
}