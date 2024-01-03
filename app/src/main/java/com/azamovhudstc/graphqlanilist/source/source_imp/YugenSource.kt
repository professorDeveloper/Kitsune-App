/*
 *  Created by Azamov X ㋡ on 1/3/24, 12:39 AM
 *  Copyright (c) 2024 . All rights reserved.
 *  Last modified 1/3/24, 12:39 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.source.source_imp

import com.azamovhudstc.graphqlanilist.data.model.AnimeStreamLink
import com.azamovhudstc.graphqlanilist.source.AnimeSource
import com.azamovhudstc.graphqlanilist.utils.Utils.getJsoup
import com.azamovhudstc.graphqlanilist.utils.Utils.postJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class YugenSource : AnimeSource {
    private val mainUrl = "https://yugenanime.tv"
    override suspend fun animeDetails(contentLink: String): Map<String, Map<String, String>> =
        withContext(Dispatchers.IO) {
            val url = "$mainUrl${contentLink}watch/?sort=episode"
            val doc = getJsoup(url)
            val animeContent = doc.getElementsByClass("p-10-t")
            val animeCover =
                doc.getElementsByClass("page-cover-inner").first()!!.getElementsByTag("img")
                    .attr("src")
            val animeName = animeContent.first()!!.text()
            val animDesc = animeContent[1].text()

            val subsEpCount = doc.getElementsByClass("box p-10 p-15 m-15-b anime-metadetails")
                .select("div:nth-child(6)").select("span").text()
            val epMapSub = (1..subsEpCount.toInt()).associate { it.toString() to it.toString() }
            val epMap = mutableMapOf("SUB" to epMapSub)

            try {
                val dubsEpCount = doc.getElementsByClass("box p-10 p-15 m-15-b anime-metadetails")
                    .select("div:nth-child(7)").select("span").text()
                println(dubsEpCount)
                val epMapDub = (1..dubsEpCount.toInt()).associate { it.toString() to it.toString() }
                epMap["DUB"] = epMapDub
            } catch (_: Exception) {
            }

            return@withContext epMap
        }


    override suspend fun searchAnime(searchedText: String) = withContext(Dispatchers.IO) {
        val animeList = arrayListOf<Pair<String, String>>()
        val searchUrl = "$mainUrl/discover/?q=${searchedText}"

        val doc = getJsoup(searchUrl)
        val allInfo = doc.getElementsByClass("anime-meta")
        for (item in allInfo) {
            val itemImage = item.getElementsByTag("img").attr("data-src")
            val itemName = item.getElementsByClass("anime-name").text()
            val itemLink = item.attr("href")
            val picObject = Pair(itemName, itemLink)
            animeList.add(picObject)
        }

        return@withContext animeList
    }

    override suspend fun getEpisodeInfos(showId: String): List<AllAnimeSource.EpisodeInfo>? {
        TODO("Not yet implemented")
    }


    override suspend fun trendingAnime(): ArrayList<Pair<String, String>> =
        withContext(Dispatchers.IO) {
            val animeList = arrayListOf<Pair<String, String>>()
            val doc = getJsoup(url = "$mainUrl/trending/")
            val allInfo = doc.getElementsByClass("series-item")
            for (item in allInfo) {
                val itemImage = item.getElementsByTag("img").attr("src")
                val itemName = item.getElementsByClass("series-title").text()
                val itemLink = item.attr("href")
                animeList.add(
                    Pair(
                        itemName,
                        itemLink
                    )
                )
            }
            return@withContext animeList
        }

    override suspend fun streamLink(
        animeUrl: String,
        animeEpCode: String,
        extras: List<String>?
    ): AnimeStreamLink =
        withContext(Dispatchers.IO) {
            // Get the link of episode
            val watchLink = animeUrl.replace("anime", "watch")

            val animeEpUrl =
                if (extras?.first() == "DUB")
                    "$mainUrl${
                        watchLink.dropLast(1)
                    }-dub/$animeEpCode"
                else "$mainUrl$watchLink$animeEpCode"

            var yugenEmbedLink =
                getJsoup(animeEpUrl).getElementById("main-embed")!!.attr("src")
            if (!yugenEmbedLink.contains("https:")) yugenEmbedLink = "https:$yugenEmbedLink"

            val mapOfHeaders = mutableMapOf(
                "X-Requested-With" to "XMLHttpRequest",
                "content-type" to "application/x-www-form-urlencoded; charset=UTF-8"
            )

            val apiRequest = "$mainUrl/api/embed/"
            val id = yugenEmbedLink.split("/")
            val dataMap = mapOf("id" to id[id.size - 2], "ac" to "0")

            val linkDetails = postJson(apiRequest, mapOfHeaders, dataMap)!!.asJsonObject
            val link = linkDetails["hls"].asJsonArray.first().asString
            return@withContext AnimeStreamLink(link, "", true)

        }
}
