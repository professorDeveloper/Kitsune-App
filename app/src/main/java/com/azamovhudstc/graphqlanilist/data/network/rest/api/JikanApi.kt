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