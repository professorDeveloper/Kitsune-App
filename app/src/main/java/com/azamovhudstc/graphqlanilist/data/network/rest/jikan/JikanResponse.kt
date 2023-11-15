package com.azamovhudstc.graphqlanilist.data.network.rest.jikan

data class JikanResponse(
    val `data`: List<Data>,
    val pagination: Pagination
)