/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.source.source_imp

import com.azamovhudstc.graphqlanilist.data.model.AnimeStreamLink
import com.azamovhudstc.graphqlanilist.source.AnimeSource
import com.azamovhudstc.graphqlanilist.utils.Utils.getJson
import com.azamovhudstc.graphqlanilist.utils.client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.nio.charset.StandardCharsets

@kotlinx.serialization.Serializable
private data class EpisodeInfo(
    // Episode "numbers" can have decimal values, hence float
    @SerialName("episodeIdNum") val episodeIdNum: Float,
    @SerialName("notes") val notes: String?,
    @SerialName("thumbnails") val thumbnails: List<String>?,
    @SerialName("vidInforssub") val vidInforssub: VidInfo?,
    @SerialName("vidInforsdub") val vidInforsdub: VidInfo?,
) {
    @Serializable
    data class VidInfo(
        // @SerialName("vidPath") val vidPath
        @SerialName("vidResolution") val vidResolution: Int?,
        @SerialName("vidSize") val vidSize: Double?,
    )
}

class AllAnimeSource : AnimeSource {

    private val mainAPIUrl = "https://api.allanime.day/api"
    private val apiHost = "https://api.allanime.co"
    private val referer = "https://allanime2.com/"
    private val idHash = "9d7439c90f203e534ca778c4901f9aa2d3ad42c06243ab2c5e6b79612af32028"
    private val episodeInfoHash = "c8f3ac51f598e630a1d09d7f7fb6924cff23277f354a23e473b962a367880f7d"


    override suspend fun trendingAnime(): ArrayList<Pair<String, String>> =
        withContext(Dispatchers.IO) {
            val animeList = arrayListOf<Pair<String, String>>()
            val hash = "1fc9651b0d4c3b9dfd2fa6e1d50b8f4d11ce37f988c23b8ee20f82159f7c1147"
            val url =
                """$mainAPIUrl?variables=%7B%22type%22%3A%22anime%22%2C%22size%22%3A30%2C%22dateRange%22%3A7%2C%22page%22%3A1%7D&extensions=%7B%22persistedQuery%22%3A%7B%22version%22%3A1%2C%22""" +
                        """sha256Hash%22%3A%22$hash%22%7D%7D"""
            val res =
                getJson(
                    url,
                    mapOf("Referer" to referer)
                )!!.asJsonObject["data"].asJsonObject["queryPopular"].asJsonObject["recommendations"].asJsonArray
            for (json in res) {

                val animeCard = json.asJsonObject["anyCard"].asJsonObject
                val name = animeCard["name"].asString
                val image = allAnimeImage(animeCard["thumbnail"].asString)
                println(name.toString())
                val id = animeCard["_id"].asString
                animeList.add(Pair<String, String>(name, id))
            }
            return@withContext animeList
        }

    override suspend fun searchAnime(searchedText: String) = withContext(Dispatchers.IO) {
        val animeList = arrayListOf<Pair<String, String>>()
        val hash = "06327bc10dd682e1ee7e07b6db9c16e9ad2fd56c1b769e47513128cd5c9fc77a"
        val url =
            """$mainAPIUrl?variables=%7B%22search%22%3A%7B%22allowAdult%22%3Afalse%2C%22allowUnknown%22%3Afalse%2C%22query%22%3A%22${
                searchedText.replace(
                    "+",
                    "%20"
                )
            }%22%7D%2C%22limit%22%3A26%2C%22page%22%3A1%2C%22translationType%22%3A%22sub%22%2C%22countryOrigin%22%3A%22ALL%22%7D&extensions=%7B%22persistedQuery%22%3A%7B%22version%22%3A1%2C%22""" +
                    """sha256Hash%22%3A%22$hash%22%7D%7D"""

        val res =
            getJson(
                url,
                mapOf("Referer" to referer)
            )!!.asJsonObject["data"].asJsonObject["shows"].asJsonObject["edges"].asJsonArray
        for (json in res) {
            val name = json.asJsonObject["name"].asString
            val image = allAnimeImage(json.asJsonObject["thumbnail"].asString)
            val id = json.asJsonObject["_id"].asString
            animeList.add(Pair(name, id))
            println(name.toString())
            println(image .toString())
        }
        return@withContext animeList
    }


    override suspend fun getEpisodeInfos(showId: String): List<EpisodeInfo>? {
        val variables = """{"_id": "$showId"}"""
        val show = graphqlQuery(variables, idHash).data?.show
        if (show != null) {
            val epCount = show.availableEpisodes.sub
            val epVariables =
                """{"showId":"$showId","episodeNumStart":0,"episodeNumEnd":${epCount}}"""
            return graphqlQuery(
                epVariables,
                episodeInfoHash
            ).data?.episodeInfos
        }
        return null
    }

    private suspend fun graphqlQuery(variables: String, persistHash: String): Query {
        val extensions = """{"persistedQuery":{"version":1,"sha256Hash":"$persistHash"}}"""
        val res = client.get(
            "$apiHost/allanimeapi",
            params = mapOf(
                "variables" to variables,
                "extensions" to extensions
            )
        ).parsed<Query>()
        if (res.data == null) throw Exception("Var : $variables\nError : ${res.errors!![0].message}")
        return res
    }

    override suspend fun animeDetails(contentLink: String): Map<String, Map<String, String>> =
        withContext(Dispatchers.IO) {
            val hash = "9d7439c90f203e534ca778c4901f9aa2d3ad42c06243ab2c5e6b79612af32028"
            val url =
                "$mainAPIUrl?variables=%7B%22_id%22%3A%22${contentLink}%22%7D&extensions=%7B%22persistedQuery%22%3A%7B%22version%22%3A1%2C%22" +
                        """sha256Hash%22%3A%22$hash%22%7D%7D"""

            println(url)
            var allEps = emptyMap<String,Map<String,String>>()
            val res = getJson(url, mapOf("Referer" to referer))!!.asJsonObject
            val data = res.asJsonObject["data"].asJsonObject["show"].asJsonObject
            val animeCover = allAnimeImage(data["thumbnail"].asString)
            println(animeCover)

            if (data != null) {
                val subNum = data["lastEpisodeInfo"].asJsonObject["sub"].asJsonObject["episodeString"].asString
                val subEpMap = (1..subNum.toInt()).associate { it.toString() to it.toString() }
                allEps = mutableMapOf("SUB" to subEpMap)
                try {
                    val dubNum =
                        data["lastEpisodeInfo"].asJsonObject["dub"].asJsonObject["episodeString"].asString
                    val dubEpMap = (1..dubNum.toInt()).associate { it.toString() to it.toString() }
                    allEps["DUB"] = dubEpMap
                } catch (_: Exception) {
                }

            }

            return@withContext allEps
        }

    override suspend fun streamLink(
        animeUrl: String,
        animeEpCode: String,
        extras: List<String>?
    ): AnimeStreamLink =
        withContext(Dispatchers.IO) {

            println(animeUrl)
            println(animeEpCode)

            val type = if (extras?.first() == "DUB") "dub" else "sub"
            println(type)
            val hash = "5f1a64b73793cc2234a389cf3a8f93ad82de7043017dd551f38f65b89daa65e0"
            val url =
                """$mainAPIUrl?variables=%7B%22showId%22%3A%22$animeUrl%22%2C%22translationType%22%3A%22$type%22%2C%22episodeString%22%3A%22$animeEpCode%22%7D&extensions=%7B%22persistedQuery%22%3A%7B%22version%22%3A1%2C%22""" +
                        """sha256Hash%22%3A%22$hash%22%7D%7D"""
            println(url)
            val res =
                getJson(
                    url,
                    mapOf("Referer" to referer)
                )!!.asJsonObject["data"].asJsonObject["episode"].asJsonObject["sourceUrls"].asJsonArray
            val sortedSources =
                res.sortedBy { if (!it.asJsonObject["priority"].isJsonNull) it.asJsonObject["priority"].asDouble else 0.0 }
                    .reversed()
            println(sortedSources)
            println("sorted")
            val sortedList = arrayListOf<String>()
            for (sourceUrlHolder in sortedSources) {
                var sourceUrl = sourceUrlHolder.asJsonObject["sourceUrl"].asString
                if (!sourceUrl.startsWith("http")) sourceUrl = sourceUrl.decodeHash()
                val sourceName = sourceUrlHolder.asJsonObject["sourceName"].asString
                if (isThese(sourceName) || isThese(sourceUrl)) continue
                sortedList.add(sourceUrl)
            }
            println("======= sortedList =====")
            sortedList.forEachIndexed { index, item ->
                println("${index + 1}) $item")
            }
            println("======= =====")
            val sourceUrl = sortedList.first()
            if (sourceUrl.contains("apivtwo")) {
                val apiUrl = "https://embed.ssbcontent.site"
                println(apiUrl)
                println(
                    "$apiUrl${
                        sourceUrl.replace("clock", "clock.json")
                    }"
                )
                val allLinks = getJson(
                    "$apiUrl${
                        sourceUrl.replace("clock", "clock.json")
                    }"
                )!!.asJsonObject["links"].asJsonArray
                println(allLinks)
                val firstLink = allLinks.first().asJsonObject
                println(firstLink)
                if (firstLink.has("portData") && firstLink["portData"].asJsonObject.has("streams")) {
                    for (link in firstLink["portData"].asJsonObject["streams"].asJsonArray) {
                        if (link.toString().contains("dash")) continue
                        if (!link.asJsonObject["hardsub_lang"].asString.contains("en")) continue
                        return@withContext AnimeStreamLink(
                            link.asJsonObject["url"].asString,
                            "",
                            link.toString().contains("hls")
                        )
                    }
                }
                println("link = $firstLink")

                val isHls = firstLink.has("hls") && firstLink["hls"].asBoolean
                val streamUrl =
                    if (firstLink.has("rawUrls")) firstLink["rawUrls"].asJsonObject["vids"].asJsonArray.first().asJsonObject["url"].asString else firstLink["link"].asString
                println()
                return@withContext AnimeStreamLink(
                    streamUrl,
                    "",
                    isHls
                )
            }

            return@withContext AnimeStreamLink(
                sourceUrl,
                "",
                res.first().toString().contains("hls")
            )
        }


    private fun decrypt(target: String): String {
        val byteArray = target.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        for (i in byteArray.indices) {
            byteArray[i] = (byteArray[i].toInt() xor 56).toByte()
        }
        return String(byteArray, StandardCharsets.UTF_8)
    }

    private fun String.decodeHash(): String {
        if (startsWith('-')) return decrypt(this.substringAfterLast('-'))
        if (startsWith('#')) return decrypt(this.substringAfterLast('#'))
        return this
    }

    private fun isThese(url: String): Boolean {
        val unwantedSources = listOf(
            "goload",
            "filemoon",
            "streamwish",
            "goone.pro",
            "playtaku",
            "streamsb",
            "ok.ru",
            "streamlare",
            "mp4upload",
            "Ak",
            "fast4speed"
        )
        unwantedSources.forEach { source ->
            if (url.contains(source)) return true
        }
        return false
    }

    private fun allAnimeImage(imageUrl: String) =
        if (imageUrl.contains("kickassanime")) "https://wp.youtube-anime.com/${
            imageUrl.replace(
                "https://",
                ""
            )
        }?w=250"
        else if (imageUrl.contains("_Show_")) "https://wp.youtube-anime.com/aln.youtube-anime.com/images/${
            imageUrl.replaceAfterLast(
                ".",
                "css"
            )
        }"
        else imageUrl

    @Serializable
    private data class Query(
        @SerialName("data") var data: Data?,
        var errors: List<Error>?
    ) {

        @Serializable
        data class Error(
            var message: String
        )

        @Serializable
        data class Data(
            @SerialName("shows") val shows: ShowsConnection?,
            @SerialName("show") val show: Show?,
            @SerialName("episodeInfos") val episodeInfos: List<EpisodeInfo>?,
            @SerialName("episode") val episode: AllAnimeEpisode?,
        )

        @Serializable
        data class ShowsConnection(
            @SerialName("edges") val edges: List<Show>
        )

        @Serializable
        data class Show(
            @SerialName("_id") val id: String,
            @SerialName("name") val name: String,
            @SerialName("englishName") val englishName: String?,
            @SerialName("nativeName") val nativeName: String?,
            @SerialName("thumbnail") val thumbnail: String?,
            @SerialName("availableEpisodes") val availableEpisodes: AvailableEpisodes,
            @SerialName("altNames") val altNames: List<String>?
        )

        @Serializable
        data class AvailableEpisodes(
            @SerialName("sub") val sub: Int,
            @SerialName("dub") val dub: Int,
            // @SerialName("raw") val raw: Int,
        )

        @Serializable
        data class AllAnimeEpisode(
            @SerialName("sourceUrls") var sourceUrls: List<SourceUrl>
        )

        @Serializable
        data class SourceUrl(
            val sourceUrl: String,
            val sourceName: String,
            val type: String
        )
    }

    @Serializable
    data class EpisodeInfo(
        // Episode "numbers" can have decimal values, hence float
        @SerialName("episodeIdNum") val episodeIdNum: Float,
        @SerialName("notes") val notes: String?,
        @SerialName("thumbnails") val thumbnails: List<String>?,
        @SerialName("vidInforssub") val vidInforssub: VidInfo?,
        @SerialName("vidInforsdub") val vidInforsdub: VidInfo?,
    ) {
        @Serializable
        data class VidInfo(
            // @SerialName("vidPath") val vidPath
            @SerialName("vidResolution") val vidResolution: Int?,
            @SerialName("vidSize") val vidSize: Double?,
        )
    }

    @Serializable
    private data class VideoResponse(
        val links: List<Link>? = null
    ) {
        @Serializable
        data class Link(
            val link: String? = null,
            val crIframe: Boolean? = null,
            val portData: PortData? = null,
            val resolutionStr: String? = null,
            val hls: Boolean? = null,
            val mp4: Boolean? = null,
            val subtitles: List<Subtitle>? = null
        )

        @Serializable
        data class PortData(
            val streams: List<Stream>? = null
        )

        @Serializable
        data class Stream(
            val format: String? = null,
            val url: String? = null,

            @SerialName("audio_lang")
            val audioLang: String? = null,

            @SerialName("hardsub_lang")
            val hardsubLang: String? = null
        )

        @Serializable
        data class Subtitle(
            val lang: String? = null,
            val src: String? = null,
            val label: String? = null,
            val default: String? = null
        )
    }


}
