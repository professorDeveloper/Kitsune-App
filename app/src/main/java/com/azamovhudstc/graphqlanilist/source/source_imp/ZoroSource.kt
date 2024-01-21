package com.azamovhudstc.graphqlanilist.source.source_imp

import android.util.Base64
import com.azamovhudstc.graphqlanilist.data.model.AnimeStreamLink
import com.azamovhudstc.graphqlanilist.data.model.SourceModel
import com.azamovhudstc.graphqlanilist.source.AnimeSource
import com.azamovhudstc.graphqlanilist.utils.Utils.getJson
import com.azamovhudstc.graphqlanilist.utils.Utils.getJsoup
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.jsoup.Jsoup
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class ZoroSource : AnimeSource {
    override suspend fun animeDetails(contentLink: String): Map<String,Map<String,String>> = withContext(Dispatchers.IO) {

            // Get Ep List
            val animeCode = contentLink.split("-").last()
            val url = "https://zoro.to/ajax/v2/episode/list/${animeCode}"
            val html = getJson(url)!!.asJsonObject.get("html")!!.asString
            val eps = Jsoup.parse(html).select(".ss-list > a[href].ssl-item.ep-item")
            val epMap = mutableMapOf<String, String>()
            eps.forEach { ep ->
                epMap[ep.attr("data-number")] = ep.attr("data-id")
            }

            val animeUrl = "http://zoro.to/${contentLink}"
            val doc = getJsoup(animeUrl)

            val animeCover = doc.selectFirst(".anisc-poster img")?.attr("src").toString()
            val animeName = doc.selectFirst(".anisc-detail > .film-name")?.text().toString()
            val animDesc = doc.selectFirst(".film-description.m-hide > .text")?.text().toString()

            return@withContext mapOf("ZORO" to epMap)
        }


    override suspend fun searchAnime(searchedText: String) = withContext(Dispatchers.IO) {
        val url = "https://zoro.to/search?keyword=${searchedText}"
        val simpleAnimeList = arrayListOf<SourceModel>()

        val doc = getJsoup(url)
        val animeList = doc.select("div.flw-item")
        for (item in animeList) {
            val animeName = item.getElementsByClass("dynamic-name").text()
            val animeImageURL = item.getElementsByTag("img").attr("data-src")
            val animeLink = item.getElementsByClass("dynamic-name").attr("href")
            simpleAnimeList.add(
                SourceModel(
                    animeName,
                    animeLink,
                    animeImageURL
                )
            )
        }
        return@withContext simpleAnimeList
    }

     suspend fun latestAnime(): ArrayList<Pair<String,String>> =
        withContext(Dispatchers.IO) {
            val simpleAnimeList = arrayListOf<Pair<String,String>>()
            val url = "https://zoro.to/recently-updated"

            val doc = getJsoup(url)
            val animeList = doc.select("div.flw-item")
            for (item in animeList) {
                val animeImageURL = item.getElementsByTag("img").attr("data-src")
                val animeName = item.getElementsByClass("dynamic-name").text()
                val animeLink = item.getElementsByClass("dynamic-name").attr("href")
                simpleAnimeList.add(
                    Pair(
                        animeName,
                        animeLink
                    )
                )
            }
            return@withContext simpleAnimeList
        }

    override suspend fun trendingAnime(): ArrayList<Pair<String,String>> =
        withContext(Dispatchers.IO) {
            val simpleAnimeList = arrayListOf<Pair<String,String>>()
            val url = "https://zoro.to/top-airing"

            val doc = getJsoup(url)
            val animeList = doc.select("div.flw-item")
            for (item in animeList) {
                val animeImageURL = item.getElementsByTag("img").attr("data-src")
                val animeName = item.getElementsByClass("dynamic-name").text()
                val animeLink = item.getElementsByClass("dynamic-name").attr("href")
                simpleAnimeList.add(
                    Pair(
                        animeName,
                        animeLink
                    )
                )
            }
            return@withContext simpleAnimeList
        }

    override suspend fun streamLink(
        animeUrl: String,
        animeEpCode: String,
        extras: List<String>?
    ): AnimeStreamLink =
        withContext(Dispatchers.IO) {
            val url = "https://zoro.to/ajax/v2/episode/servers?episodeId=$animeEpCode"
            val html = getJson(url)!!.asJsonObject.get("html")!!.asString
            val items = Jsoup.parse(html).select(".server-item[data-type][data-id]")
            val servers = items.map {
                Pair(
                    if (it.attr("data-type") == "sub") "sub" else "dub",
                    it.attr("data-id")
                )
            }

            val rapidLinks = arrayListOf<String>()
            servers.distinctBy { it.second }.map {
                val link =
                    "https://zoro.to/ajax/v2/episode/sources?id=${it.second}"
                if (it.first == "sub")
                    rapidLinks.add(getJson(link)!!.asJsonObject.get("link")!!.asString)
            }
            val rapidUrl = rapidLinks.firstOrNull { it.contains("rapid") }
                ?: ""
            val jsonLink = "https://rapid-cloud.co/ajax/embed-6/getSources?id=${
                rapidUrl.replaceBeforeLast(
                    "/embed-6/",
                    ""
                ).replace("/embed-6/", "").replace("?z=", "")
            }".replace("?vast=1", "")
            val json = getJson(
                jsonLink, mapOfHeaders = mapOf(
                    "Referer" to "https://zoro.to/",
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:94.0) Gecko/20100101 Firefox/94.0"
                )
            )!!.asJsonObject
            val source = if (!json["sources"]!!.isJsonArray) {
                val key =
                    getJson("https://raw.githubusercontent.com/consumet/rapidclown/main/key.txt")!!.asString
                val decryptedText = decrypt(json["sources"]!!.asString, key)
                JsonParser.parseString(decryptedText).asJsonArray
            } else {
                json["sources"]!!.asJsonArray
            }


            val m3u8 = source[0].asJsonObject["file"].toString().trim('"')
            val subtitle = mutableMapOf<String, String>()

            json["tracks"]!!.asJsonArray.forEach {
                if (it.asJsonObject["kind"].toString().trim('"') == "captions")
                    subtitle[it.asJsonObject["label"].toString().trim('"')] =
                        it.asJsonObject["file"].toString().trim('"')
            }

            val sId = ZoroSource().wss()

            return@withContext AnimeStreamLink(
                m3u8, subtitle["English"]!!, true,
                hashMapOf(
                    "sid" to sId,
                    "referer" to "https://rapid-cloud.co/",
                    "origin" to "https://rapid-cloud.co"
                )
            )
        }

    private fun decryptSourceUrl(decryptionKey: ByteArray, sourceUrl: String): String {
        val cipherData = Base64.decode(sourceUrl, Base64.DEFAULT)
        val encrypted = cipherData.copyOfRange(16, cipherData.size)
        val aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding")

        Objects.requireNonNull(aesCBC).init(
            Cipher.DECRYPT_MODE, SecretKeySpec(
                decryptionKey.copyOfRange(0, 32),
                "AES"
            ),
            IvParameterSpec(decryptionKey.copyOfRange(32, decryptionKey.size))
        )
        val decryptedData = aesCBC!!.doFinal(encrypted)
        return String(decryptedData, StandardCharsets.UTF_8)
    }

    private fun md5(input: ByteArray): ByteArray {
        return MessageDigest.getInstance("MD5").digest(input)
    }

    private fun generateKey(salt: ByteArray, secret: ByteArray): ByteArray {
        var key = md5(secret + salt)
        var currentKey = key
        while (currentKey.size < 48) {
            key = md5(key + secret + salt)
            currentKey += key
        }
        return currentKey
    }

    private fun decrypt(input: String, key: String): String {
        return decryptSourceUrl(
            generateKey(
                Base64.decode(input, Base64.DEFAULT).copyOfRange(8, 16),
                key.toByteArray()
            ), input
        )
    }

    fun String.findBetween(a: String, b: String): String? {
        val start = this.indexOf(a)
        val end = if (start != -1) this.indexOf(b, start) else return null
        return if (end != -1) this.subSequence(start, end).removePrefix(a).removeSuffix(b)
            .toString() else null
    }

    private val okHttpClient = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    private suspend fun wss(): String = withContext(Dispatchers.IO) {
        var sId = getJsoup("https://api.enime.moe/tool/rapid-cloud/server-id").text()
        if (sId.isEmpty()) {
            val latch = CountDownLatch(1)
            val listener = object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    webSocket.send("40")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    when {
                        text.startsWith("40") -> {
                            sId = text.findBetween("40{\"sid\":\"", "\"}") ?: ""
                            latch.countDown()
                        }
                        text == "2" -> webSocket.send("3")
                    }
                }
            }
            okHttpClient.newWebSocket(
                Request.Builder()
                    .url("wss://ws1.rapid-cloud.co/socket.io/?EIO=4&transport=websocket").build(),
                listener
            )
            latch.await(30, TimeUnit.SECONDS)
        }
        return@withContext sId
    }
}