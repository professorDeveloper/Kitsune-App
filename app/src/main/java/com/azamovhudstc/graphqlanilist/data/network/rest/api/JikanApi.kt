/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.data.network.rest.api

import com.azamovhudstc.graphqlanilist.data.network.rest.jikan.JikanResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JikanApi {
    @GET("/v4/anime/{id}/videos/episodes")
    fun getEpisodesById(
        @Path("id") id: Int,
        @Query("page") page: Int
    ): Call<JikanResponse>

}