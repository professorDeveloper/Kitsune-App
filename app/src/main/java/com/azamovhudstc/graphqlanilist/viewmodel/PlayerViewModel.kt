/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.viewmodel

import android.app.Application
import android.media.session.PlaybackState
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.widget.Toast
import androidx.lifecycle.*
import com.azamovhudstc.graphqlanilist.data.model.AnimeStreamLink
import com.azamovhudstc.graphqlanilist.source.AnimeSource
import com.azamovhudstc.graphqlanilist.source.SourceSelector
import com.azamovhudstc.graphqlanilist.ui.activity.PlayerActivity
import com.azamovhudstc.graphqlanilist.utils.logError
import com.azamovhudstc.graphqlanilist.utils.logMessage
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.LoadEventInfo
import com.google.android.exoplayer2.source.MediaLoadData
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.SingleSampleMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.MimeTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val app: Application,
    val player: ExoPlayer,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var mediaSession: MediaSessionCompat =
        MediaSessionCompat(app, "AnimeScrap Media Session")
    private val _animeStreamLink: MutableLiveData<AnimeStreamLink> = MutableLiveData()
    private val animeStreamLink: LiveData<AnimeStreamLink> = _animeStreamLink
    private val isAutoPlayEnabled = true
    private val isVideoCacheEnabled = true

    val isLoading = MutableLiveData(true)
    val keepScreenOn = MutableLiveData(false)
    val showSubsBtn = MutableLiveData(false)
    val playNextEp = MutableLiveData(false)
    val isError = MutableLiveData(false)


    private var qualityMapUnsorted: MutableMap<String, Int> = mutableMapOf()
    var qualityMapSorted: MutableMap<String, Int> = mutableMapOf()
    var qualityTrackGroup: com.google.android.exoplayer2.Tracks.Group? = null

    private var mediaSessionConnector: MediaSessionConnector = MediaSessionConnector(mediaSession)

    private var simpleCache: SimpleCache? = null
    private val databaseProvider =
        com.google.android.exoplayer2.database.StandaloneDatabaseProvider(app)

    private val savedDone = savedStateHandle.getStateFlow("done", false)

    init {
        player.prepare()
        player.playWhenReady = true
        mediaSessionConnector.setPlayer(player)
        mediaSession.isActive = true
        player.addListener(getCustomPlayerListener())
        player.addAnalyticsListener(object :AnalyticsListener{
            override fun onLoadError(
                eventTime: AnalyticsListener.EventTime,
                loadEventInfo: LoadEventInfo,
                mediaLoadData: MediaLoadData,
                error: IOException,
                wasCanceled: Boolean
            ) {
                logMessage(error.message)
                logError(error.cause)
            }
        })


        // Cache
        simpleCache?.release()
        simpleCache = SimpleCache(
            File(
                app.cacheDir,
                "exoplayer"
            ).also { it.deleteOnExit() }, // Ensures always fresh file
            LeastRecentlyUsedCacheEvictor(300L * 1024L * 1024L),
            databaseProvider
        )
    }

    private fun getCustomPlayerListener(): Player.Listener {
        return object : Player.Listener {

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == PlaybackState.STATE_NONE || playbackState == PlaybackState.STATE_CONNECTING || playbackState == PlaybackState.STATE_STOPPED)
                    isLoading.postValue(true)
                else
                    isLoading.postValue(false)
                super.onPlaybackStateChanged(playbackState)
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                isError.postValue(true)
                Toast.makeText(app, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                keepScreenOn.postValue(isPlaying)
                val progress = player.duration - player.currentPosition
                if (progress <= 0 && isAutoPlayEnabled && !isPlaying)
                    playNextEp.postValue(true)
            }

            override fun onTracksChanged(tracks: com.google.android.exoplayer2.Tracks) {
                // Update UI using current tracks.
                for (trackGroup in tracks.groups) {
                    // Group level information.
                    if (trackGroup.type == C.TRACK_TYPE_VIDEO) {
                        for (i in 0 until trackGroup.length) {
                            val trackFormat = trackGroup.getTrackFormat(i).height
                            if (trackGroup.isTrackSupported(i) && trackGroup.isTrackSelected(i)) {
                                qualityMapUnsorted["${trackFormat}p"] = i
                            }
                        }
                        qualityMapUnsorted.entries.sortedBy { it.key.replace("p", "").toInt() }
                            .reversed().forEach { qualityMapSorted[it.key] = it.value }

                        qualityTrackGroup = trackGroup
                    }

                }
            }
        }

    }

    fun setAnimeLink(
        sourceType :String,
        animeUrl: String,
        animeEpCode: String,
        extras: List<String>,
        getNextEp: Boolean = false
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                println("STREAM GET LINK")
                if ("aniworld"!= PlayerActivity.sourceType){
                    val animeSource: AnimeSource = SourceSelector(app).getSelectedSource(PlayerActivity.sourceType)
                    animeSource.streamLink(animeUrl, animeEpCode, extras).apply {
                        _animeStreamLink.postValue(this@apply)
                        withContext(Dispatchers.Main) {
                            if (!savedDone.value || getNextEp) {
                                println("prepare Media Source")
                                prepareMediaSource()
                                savedStateHandle["done"] = true
                            }
                        }
                    }
                }else{
                    val animeSource: AnimeSource = SourceSelector(app).getSelectedSource(PlayerActivity.sourceType)
                    animeSource.streamLink(animeEpCode, animeUrl, extras).apply {
                        _animeStreamLink.postValue(this@apply)
                        withContext(Dispatchers.Main) {
                            if (!savedDone.value || getNextEp) {
                                println("prepare Media Source")
                                prepareMediaSource()
                                savedStateHandle["done"] = true
                            }
                        }
                    }

                }
            }
        }

    }

    private fun releaseCache() {
        simpleCache?.release()
        simpleCache = null
    }

    private fun setMediaSource(mediaSource: MediaSource) {
        println("Set media Source")
        player.stop()
        player.prepare()
        qualityMapSorted = mutableMapOf()
        qualityMapUnsorted = mutableMapOf()
        qualityTrackGroup = null
        player.setMediaSource(mediaSource)
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
        releaseCache()
    }

    private fun releasePlayer() {
        player.release()
        mediaSession.release()
    }

    private fun prepareMediaSource() {
        if (animeStreamLink.value == null) return
        var mediaSource: MediaSource
        val mediaItem: MediaItem
        val headerMap =  mapOf(
            "Cookie" to "_ym_uid=1664171290829008916; \"_pubcid\"=439b1e7c-eab3-4392-a9a7-19b1e53fe9f3; _ym_d=1696009917; __gads=ID=47342de96a689496-224c06c4fbdd00d6:T=1685651803:RT=1699104092:S=ALNI_Mb2ZhtSMyfS5P7PZrwc7eQv5t2WRg; __gpi=UID=00000c2ace922f58:T=1685651803:RT=1699104092:S=ALNI_MZzapclV2KKmb9oTHGcM6MVmi-EBg; comment_name=Foydalanuvchi; _pbjs_userid_consent_data=3524755945110770; _gid=GA1.2.704416453.1705347575; adrcid=ACr-r0sIPrgrh7iAg-Dg5rQ; adrcid_cd=1705407018194; _ym_isad=1; ci_session=rku1vq97bd4cdr8e1piekobjspkeuedl; _ga_XVBVMVW651=GS1.1.1705431478.202.1.1705433781.0.0.0; _ga=GA1.2.504275464.1685651802",
            "Connection" to "keep-alive",
            "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/237.84.2.178 Safari/537.36",
            "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
        )


        println(headerMap)
        val dataSourceFactory
                : DataSource.Factory = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36")
            .setDefaultRequestProperties(headerMap)
            .setReadTimeoutMs(20000)
            .setConnectTimeoutMs(20000)

        if (isVideoCacheEnabled) {

            val cacheFactory = CacheDataSource.Factory().apply {
                setCache(simpleCache!!)
                setUpstreamDataSourceFactory(dataSourceFactory)
            }
            mediaItem =
                MediaItem.fromUri(animeStreamLink.value!!.link)
            mediaSource = if (animeStreamLink.value!!.isHls) {
                HlsMediaSource.Factory(cacheFactory)
                    .setAllowChunklessPreparation(true)
                    .createMediaSource(mediaItem)
            } else {
                ProgressiveMediaSource.Factory(cacheFactory)
                    .createMediaSource(mediaItem)
            }
        } else {
            mediaItem =
                MediaItem.fromUri(animeStreamLink.value!!.link)
            mediaSource = if (animeStreamLink.value!!.isHls) {
                HlsMediaSource.Factory(dataSourceFactory)
                    .setAllowChunklessPreparation(true)
                    .createMediaSource(mediaItem)
            } else {
                ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem)
            }
        }

        if (animeStreamLink.value!!.subsLink.isNotBlank()) {
            logMessage(animeStreamLink.value!!.subsLink)
            showSubsBtn.postValue(true)
            val subtitleMediaSource = SingleSampleMediaSource.Factory(dataSourceFactory)
                .createMediaSource(
                    MediaItem.SubtitleConfiguration.Builder(Uri.parse(animeStreamLink.value!!.subsLink))
                        .apply {
                            if (animeStreamLink.value!!.subsLink.contains("srt"))
                                setMimeType(MimeTypes.APPLICATION_SUBRIP)
                            else
                                setMimeType(MimeTypes.TEXT_VTT)
                            setLanguage("en")
                            setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                        }.build(),
                    C.TIME_UNSET
                )
            mediaSource = MergingMediaSource(mediaSource, subtitleMediaSource)
        } else {
            showSubsBtn.postValue(false)
        }
        setMediaSource(mediaSource)
    }

}