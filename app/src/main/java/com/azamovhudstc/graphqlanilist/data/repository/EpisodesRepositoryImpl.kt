package com.azamovhudstc.graphqlanilist.data.repository

import com.azamovhudstc.graphqlanilist.data.network.rest.api.JikanApi
import com.azamovhudstc.graphqlanilist.data.network.rest.jikan.JikanResponse
import com.azamovhudstc.graphqlanilist.domain.repository.EpisodesRepository
import com.azamovhudstc.graphqlanilist.utils.logError
import com.azamovhudstc.graphqlanilist.utils.logMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class EpisodesRepositoryImpl @Inject constructor(private val api: JikanApi) : EpisodesRepository {
    private lateinit var successListener: ((JikanResponse) -> Unit)
    private lateinit var pageSuccessListener: ((JikanResponse) -> Unit)
    fun loadPageSuccessListener(listener: ((JikanResponse) -> Unit)) {
        pageSuccessListener = listener
    }
    fun loadSuccessListener(listener: ((JikanResponse) -> Unit)) {
        successListener = listener
    }

    override fun getEpisodesByIdPage(id: Int, page: Int) {
        val response = api.getEpisodesById(id, page)
        response.enqueue(object :Callback<JikanResponse>{
            override fun onResponse(call: Call<JikanResponse>, response: Response<JikanResponse>) {
                if (response.isSuccessful) {
                    pageSuccessListener.invoke(response.body()!!)
                } else {
                    logError(Exception(response.errorBody()!!.string()))
                }
            }

            override fun onFailure(call: Call<JikanResponse>, t: Throwable) {
                logError(t)
            }

        })

    }

    override fun getEpisodesById(id: Int) {
        val response = api.getEpisodesById(id, 1)

        response.enqueue(object : Callback<JikanResponse> {
            override fun onResponse(call: Call<JikanResponse>, response: Response<JikanResponse>) {
                if (response.isSuccessful) {
                    successListener.invoke(response.body()!!)
                } else {
                    logError(Exception(response.errorBody()!!.string()))
                }

            }

            override fun onFailure(call: Call<JikanResponse>, t: Throwable) {
                logError(t)
            }

        })
    }
}