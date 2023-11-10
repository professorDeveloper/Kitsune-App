package com.azamovhudstc.graphqlanilist.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.data.model.ui_models.EpisodeModel
import com.azamovhudstc.graphqlanilist.domain.repository.DetailsRepository
import com.azamovhudstc.graphqlanilist.domain.repository.FavoriteRepository
import com.azamovhudstc.graphqlanilist.domain.repository.UserRepository
import com.azamovhudstc.graphqlanilist.type.MediaListStatus
import com.azamovhudstc.graphqlanilist.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val detailsRepository: DetailsRepository,
    private val userRepository: UserRepository,
    private val favoriteRepository: FavoriteRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    val animeMetaModel = MutableStateFlow(AniListMedia())
    val reverseState = MutableStateFlow(false)

    val episodeList: StateFlow<EpisodeListUiState> =
        animeMetaModel.distinctUntilChanged { old, _ ->
            old != AniListMedia()
        }.flatMapLatest { media ->
            favoriteRepository.getGogoUrlFromAniListId(media.idAniList)
                .asResult()
                .flatMapLatest { result ->
                    when (result) {
                        is Result.Error -> flowOf(EpisodeListUiState.Error)
                        Result.Loading -> flowOf(EpisodeListUiState.Loading)
                        is Result.Success -> {
                            detailsRepository.fetchEpisodeList(
                                episodeUrl = result.data,
                                malId = media.idMal.or1(),
                                extra = listOf(media.idMal)
                            ).map { episodes ->
                                // Split the list of episodes into chunks of 50 or less
                                val episodeChunksTitles = episodes.chunked(50) {
                                    val firstEpisodeNumber =
                                        it.first().getEpisodeNumberOnly()!!.toInt()
                                    val lastEpisodeNumber =
                                        it.last().getEpisodeNumberOnly()!!.toInt()
                                    "Episodes $firstEpisodeNumber - $lastEpisodeNumber"
                                }
                                EpisodeListUiState.Success(
                                    episodeChunks = episodes.chunked(50),
                                    chunkTitles = episodeChunksTitles
                                )
                            }
                        }
                    }
                }
        }.flowOn(Dispatchers.Default)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                EpisodeListUiState.Loading
            )

    /**
     * > The function updates the anime as favorite in the AniList website
     */
    fun updateAnimeFavorite() {
        viewModelScope.launch(ioDispatcher) {

        }
    }

    fun changeAnimeStatus(status: MediaListStatus) {
        viewModelScope.launch(ioDispatcher) {
            animeMetaModel.flatMapLatest {
                detailsRepository.changeAnimeStatus(it.idAniList, status)
                    .ifChanged()
                    .catch { error -> logError(error) }
            }.collect()
        }
    }
}

sealed interface EpisodeListUiState {
    object Loading : EpisodeListUiState
    object Error : EpisodeListUiState
    data class Success(
        val episodeChunks: List<List<EpisodeModel>>,
        val chunkTitles: List<String>
    ) : EpisodeListUiState
}