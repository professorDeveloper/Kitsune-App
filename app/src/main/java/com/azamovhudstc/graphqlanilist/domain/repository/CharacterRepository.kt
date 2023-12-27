/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.domain.repository

import com.azamovhudstc.graphqlanilist.CharacterDataByIDQuery
import com.azamovhudstc.graphqlanilist.data.model.ui_models.CharacterMedia
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getCharacterById(id: Int): Flow<Result<CharacterMedia>>
}