package com.azamovhudstc.graphqlanilist.domain.repository

import android.icu.util.VersionInfo
import com.azamovhudstc.graphqlanilist.data.model.ui_models.ChangedMediaResponse
import com.azamovhudstc.graphqlanilist.data.model.ui_models.EpisodeModel
import com.azamovhudstc.graphqlanilist.parser.GoGoParser
import com.azamovhudstc.graphqlanilist.type.MediaListStatus
import com.azamovhudstc.graphqlanilist.utils.Constants
import kotlinx.coroutines.flow.Flow

interface DetailsRepository {
    val parser: GoGoParser

    fun fetchEpisodeList(
        header: Map<String, String> = Constants.getNetworkHeader(),
        extra: List<Any?> = emptyList(),
        malId: Int,
        episodeUrl: String
    ): Flow<List<EpisodeModel>>

    fun changeAnimeStatus(
        mediaId: Int,
        status: MediaListStatus
    ): Flow<ChangedMediaResponse>

    fun getUpdateVersionInfo(): Flow<VersionInfo>
}
