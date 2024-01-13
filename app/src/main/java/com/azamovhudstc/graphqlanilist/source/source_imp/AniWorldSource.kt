/*
 *  Created by Azamov X ㋡ on 1/14/24, 12:50 AM
 *  Copyright (c) 2024 . All rights reserved.
 *  Last modified 1/14/24, 12:50 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.source.source_imp

import com.azamovhudstc.graphqlanilist.data.model.AnimeStreamLink
import com.azamovhudstc.graphqlanilist.source.AnimeSource
import com.azamovhudstc.graphqlanilist.utils.Utils.getJsoup
import com.azamovhudstc.graphqlanilist.utils.okHttpClient
import com.azamovhudstc.graphqlanilist.utils.parser
import com.azamovhudstc.scarpingtutorial.aniworld.AniworldSearchData
import com.azamovhudstc.graphqlanilist.data.model.EpisodeData
import com.azamovhudstc.graphqlanilist.utils.logMessage
import com.azamovhudstc.graphqlanilist.utils.removeEmTagsWithRegex
import com.azamovhudstc.scarpingtutorial.aniworld.AniworldSearchDataItem
import com.azamovhudstc.scarpingtutorial.aniworld.EpisodeFullData
import com.lagradost.nicehttp.Requests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception

class AniWorldSource : AnimeSource {
    private val mainUrl = "https://aniworld.to"
    override suspend fun getEpisodeInfos(showId: String): List<AllAnimeSource.EpisodeInfo>? {
        TODO("Not yet implemented")
    }


    override suspend fun trendingAnime(): ArrayList<Pair<String, String>> {
        TODO("Not yet implemented")
    }


    private suspend fun searchAnimeInAniWord(keyWord: String) =withContext(Dispatchers.IO){
        try {
            val request = Requests(baseClient = okHttpClient)
            val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "keyword",
                    keyWord,
                )
                .build()

            val data = request.post(
                "https://aniworld.to/ajax/search",
                requestBody = requestBody,
                responseParser = parser
            )
            return@withContext data.parsed<AniworldSearchData>()
        }catch (e:Exception){
            return@withContext emptyList<AniworldSearchDataItem>()
        }
    }

    override suspend fun searchAnime(text: String) =        withContext(Dispatchers.IO){
        val list = arrayListOf<Pair<String, String>>()

        searchAnimeInAniWord(text).onEach {
            list.add(Pair(it.title.removeEmTagsWithRegex(), it.link))
        }
        return@withContext list
    }

    override suspend fun animeDetails(id: String)= withContext(Dispatchers.IO) {
        val epList = animeDetailsByParsedData(id)
        val epMapSub = mutableMapOf<String, String>()
        epList.onEach {
            epMapSub.put(it.number, it.link)
        }

        val epMap = mutableMapOf("SUB" to epMapSub)
        return@withContext epMap
    }

    suspend fun animeDetailsByParsedData(parsedData: String)= withContext(Dispatchers.IO) {
        val epList = arrayListOf<EpisodeData>()
        val doc = getJsoup("$mainUrl/${parsedData}")
        val animeList = doc.getElementsByClass("hosterSiteDirectNav")
        val episodeElements =
            animeList.select("ul:has(li a[data-episode-id]) li a[data-episode-id]")
        for (episodeElement in episodeElements) {
            val number = episodeElement.text()
            val url = episodeElement.attr("href")
            epList.add(EpisodeData(number.removeEmTagsWithRegex(), url))
        }
        return@withContext epList
    }


    suspend fun setLink(url: String)= withContext(Dispatchers.IO) {
        var episodeFullData = EpisodeFullData("", "", "")
        val document = getJsoup("$mainUrl/$url")
        val firstDataLinkId =
            document.select("li[data-link-id]").firstOrNull()?.attr("data-link-id")
        val hosterElements = document.getElementsByClass("watchEpisode")
        for (element in hosterElements) {
            val hoster = element.select("i").attr("title")
            val hosterName = element.select("h4").text()
            if (hosterName == "VOE") {
                episodeFullData =
                    EpisodeFullData(hosterName, "/redirect/${firstDataLinkId.toString()}", hoster)
            }
        }

        return@withContext episodeFullData
    }

    override suspend fun streamLink(
        animeUrl: String,
        animeEpCode: String,
        extras: List<String>?
    ) = withContext(Dispatchers.IO) {

        val epFullData = setLink(animeUrl)
        val redirectLink = getAnimeRedirectLink(epFullData.hostUrl)


        logMessage(redirectLink)

        return@withContext AnimeStreamLink(redirectLink, "", true)


    }

    suspend fun getAnimeRedirectLink(redirectLink: String) = withContext(Dispatchers.IO) {
        val document = getJsoup("$mainUrl/$redirectLink")
        val scriptTags = document.select("script")

        for (scriptTag in scriptTags) {
            val scriptCode = scriptTag.html()
            if (scriptCode.contains("var sources")) {
                val hlsUrl = extractValue(scriptCode, "'hls': '(.*?)'")
                return@withContext hlsUrl
            }
        }
        return@withContext ""
    }

    fun extractValue(input: String, regex: String): String {
        val matchResult = Regex(regex).find(input)
        return matchResult?.groupValues?.get(1) ?: ""
    }
}