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
import com.azamovhudstc.graphqlanilist.utils.Utils.httpClient
import com.azamovhudstc.graphqlanilist.utils.Utils.postJson
import com.azamovhudstc.graphqlanilist.utils.parser
import com.lagradost.nicehttp.Requests
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

    suspend fun getM3u8LocationFile(): String {
        val mainUrl =
            "https://uzmovi.com/play/e4y5z5x543r244b4v2t20334m284u2t2w2s203y2t2o4u21686u5n4u4i4p2a4k4h482s5s5r4r4x5e474w5n384e2h4w5k5h2c484z3d4u503/"
        val requests = Requests(httpClient, responseParser = parser)

        val data = requests.get(
            mainUrl,

            headers = mapOf(
                "Cookie" to "_ym_uid=1664171290829008916; \"_pubcid\"=439b1e7c-eab3-4392-a9a7-19b1e53fe9f3; _ym_d=1696009917; __gads=ID=47342de96a689496-224c06c4fbdd00d6:T=1685651803:RT=1699104092:S=ALNI_Mb2ZhtSMyfS5P7PZrwc7eQv5t2WRg; __gpi=UID=00000c2ace922f58:T=1685651803:RT=1699104092:S=ALNI_MZzapclV2KKmb9oTHGcM6MVmi-EBg; comment_name=Foydalanuvchi; _pbjs_userid_consent_data=3524755945110770; _gid=GA1.2.704416453.1705347575; adrcid=ACr-r0sIPrgrh7iAg-Dg5rQ; adrcid_cd=1705407018194; _ym_isad=1; ci_session=rku1vq97bd4cdr8e1piekobjspkeuedl; _ga_XVBVMVW651=GS1.1.1705431478.202.1.1705433781.0.0.0; _ga=GA1.2.504275464.1685651802",
                "Connection" to "keep-alive",
                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/237.84.2.178 Safari/537.36",
                "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                "Accept-Encoding" to "gzip, deflate",
                "DNT" to "1",
                "host" to "uzmovi.com",
                "Accept-Language" to "ru-RU,ru;q=0.9,uz-UZ;q=0.8,uz;q=0.7,en-GB;q=0.6,en-US;q=0.5,en;q=0.4",
            ), referer = "https://uzmovi.com/"
        )

        return updateDataUrl(data.url)+"index.m3u8"
    }
}

fun updateDataUrl(dataUrl: String): String {
    // Replace 'http://' with 'https://'
    val updatedUrl = dataUrl.replace("http://", "http://")

    // Print the updated URL (you can return it or use it as needed)
    println(updatedUrl)

    return updatedUrl
}
