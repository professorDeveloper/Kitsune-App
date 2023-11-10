package com.azamovhudstc.graphqlanilist.data.repository

import android.icu.util.VersionInfo
import com.azamovhudstc.graphqlanilist.data.local.BaseClient
import com.azamovhudstc.graphqlanilist.data.local.EpisodeDao
import com.azamovhudstc.graphqlanilist.data.local.Settings
import com.azamovhudstc.graphqlanilist.data.local.UpdateClient
import com.azamovhudstc.graphqlanilist.data.model.ui_models.EnimeResponse
import com.azamovhudstc.graphqlanilist.data.model.ui_models.EpisodeModel
import com.azamovhudstc.graphqlanilist.data.model.ui_models.EpisodeWithTitle
import com.azamovhudstc.graphqlanilist.data.network.service.AniListGraphQlClient
import com.azamovhudstc.graphqlanilist.domain.repository.DetailsRepository
import com.azamovhudstc.graphqlanilist.parser.GoGoParser
import com.azamovhudstc.graphqlanilist.type.AnimeTypes
import com.azamovhudstc.graphqlanilist.type.MediaListStatus
import com.azamovhudstc.graphqlanilist.utils.convert
import com.azamovhudstc.graphqlanilist.utils.logError
import com.azamovhudstc.graphqlanilist.utils.providerFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import okhttp3.ResponseBody
import javax.inject.Inject

class DetailRepositoryImpl @Inject constructor(
    apiClients: Map<String, @JvmSuppressWildcards BaseClient>,
    private val ioDispatcher: CoroutineDispatcher,
    private val episodeDao: EpisodeDao,
    private val settings: Settings,
    private val updateClient: UpdateClient,
    private val aniListGraphQlClient: AniListGraphQlClient,
    override val parser: GoGoParser,
) : DetailsRepository {
    private val selectedAnimeProvider: BaseClient? =
        apiClients[settings.selectedProvider.name]

    override fun fetchEpisodeList(
        header: Map<String, String>,
        extra: List<Any?>,
        malId: Int,
        episodeUrl: String
    ): Flow<List<EpisodeModel>> {
        return combine(
            getListOfEpisodes(episodeUrl, extra),
            getEpisodeTitles(malId),
            getEpisodesPercentage(malId)
        ) { episodeModels,
            episodesWithTitle,
            episodeEntities ->
            episodeModels.mapIndexed { index, episodeModel ->
                if (episodeModel.getEpisodeNumberAsString() == episodesWithTitle?.getOrNull(index)?.number) {
                    episodeModel.episodeName = episodesWithTitle[index].title
                    episodeModel.isFiller = episodesWithTitle[index].isFiller
                } else {
                    episodeModel.episodeName = ""
                    episodeModel.isFiller = false
                }
                episodeModel
            }.map { episode ->
                val contentEpisode =
                    episodeEntities.firstOrNull { it.episodeUrl == episode.episodeUrl }
                if (contentEpisode != null) {
                    episode.percentage = contentEpisode.getWatchedPercentage()
                }
                episode
            }
        }.flowOn(ioDispatcher)
    }


    override fun changeAnimeStatus(mediaId: Int, status: MediaListStatus) = flow {
        emit(aniListGraphQlClient.markAnimeStatus(mediaId, status).convert())
    }

    private fun getListOfEpisodes(
        episodeUrl: String,
        extra: List<Any?>
    ) = providerFlow(settings) { provider ->
        when (provider) {
            AnimeTypes.GOGO_ANIME -> {
                val response = selectedAnimeProvider?.fetchEpisodeList<ResponseBody>(episodeUrl)
                val episodeList =
                    parser.fetchEpisodeList(response?.string().orEmpty()).reversed()
                emit(episodeList)
            }

            AnimeTypes.ENIME -> {
                val response =
                    selectedAnimeProvider?.fetchEpisodeList<EnimeResponse>(episodeUrl, extra)

                val episodeList = response?.episodes?.map {
                    EpisodeModel(it.title, "Episode ${it.number}", "")
                } ?: emptyList()
                emit(episodeList)
            }
        }
    }.catch { e ->
        logError(e)
        emit(emptyList())
    }.flowOn(ioDispatcher)

    private fun getEpisodeTitles(id: Int) = flow {
        val response =
            selectedAnimeProvider?.getEpisodeTitles<EpisodeWithTitle>(id)?.episodes?.ifEmpty { emptyList() }
        emit(response)
    }.catch { emit(emptyList()) }.flowOn(ioDispatcher)

    private fun getEpisodesPercentage(malId: Int) = episodeDao.getEpisodesByAnime(malId = malId)
        .catch { emit(emptyList()) }

    override fun getUpdateVersionInfo(): Flow<VersionInfo> {
        TODO("Not yet implemented")
    }
}