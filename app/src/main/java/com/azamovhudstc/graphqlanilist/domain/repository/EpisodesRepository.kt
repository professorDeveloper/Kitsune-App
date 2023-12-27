/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.domain.repository

import com.azamovhudstc.graphqlanilist.data.network.rest.jikan.JikanResponse
import kotlinx.coroutines.flow.Flow

interface EpisodesRepository {
    fun getEpisodesByIdPage(id:Int,page:Int)
    fun getEpisodesById(id:Int,)
}