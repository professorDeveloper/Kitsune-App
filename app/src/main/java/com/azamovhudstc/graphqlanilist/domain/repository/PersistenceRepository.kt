package com.azamovhudstc.graphqlanilist.domain.repository

import com.azamovhudstc.graphqlanilist.data.model.ui_models.EpisodeEntity
import kotlinx.coroutines.flow.Flow

interface PersistenceRepository {

    suspend fun insertEpisode(episodeEntity: EpisodeEntity)
    suspend fun updateEpisode(episodeEntity: EpisodeEntity)
    suspend fun getEpisodeContent(episodeUrl: String): Flow<EpisodeEntity>
    suspend fun isEpisodeOnDatabase(episodeUrl: String): Boolean

    var expiration: Int?
    var bearerToken: String?
    var refreshToken: String?
    var guestToken: String?
    var aniListUserId: String?
    var dns: Int?

    var iv: String?
    var key: String?
    var secondKey: String?

    fun clearStorage(triggered: () -> Unit)
}
