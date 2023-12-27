/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.domain.repository

import com.azamovhudstc.graphqlanilist.data.model.ui_models.Media
import com.azamovhudstc.graphqlanilist.data.model.ui_models.Pages
import kotlinx.coroutines.flow.Flow

interface DetailRepository {
     fun getFullDataByID(mediaId: Int): Flow<Media>
     fun getImagesByGenre(genre:String):Flow<Pages>

}