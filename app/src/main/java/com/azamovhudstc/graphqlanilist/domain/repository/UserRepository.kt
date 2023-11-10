package com.azamovhudstc.graphqlanilist.domain.repository

import com.apollographql.apollo3.api.ApolloResponse
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val bearerToken: String?
    val refreshToken: String?
    val guestToken: String?
    val isAuthenticated: Boolean
    val isGuest: Boolean
    val userId: String?
    val expiration: Int?

    fun setBearerToken(authToken: String?)
    fun setRefreshToken(refreshToken: String?)
    fun setExpirationTime(expiration: Int)
    fun setAniListUserId(sync: String?)
    fun setProvider(provider: String)
    fun clearStorage(triggered: () -> Unit)
}
