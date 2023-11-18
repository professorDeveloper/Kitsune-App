package com.azamovhudstc.graphqlanilist.utils

import android.content.Context
import android.os.Build
import android.view.View
import coil.map.Mapper
import com.google.android.material.snackbar.Snackbar
import dev.brahmkshatriya.nicehttp.Requests
import dev.brahmkshatriya.nicehttp.ResponseParser
import dev.brahmkshatriya.nicehttp.addGenericDns
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass


val defaultHeaders = mapOf(
    "User-Agent" to
            "Mozilla/5.0 (Linux; Android %s; %s) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Mobile Safari/537.36"
                .format(Build.VERSION.RELEASE, Build.MODEL)
)
lateinit var cache: Cache

lateinit var okHttpClient: OkHttpClient
lateinit var client: Requests

fun initializeNetwork(context: Context) {
    cache = Cache(
        File(context.cacheDir, "http_cache"),
        5 * 1024L * 1024L // 5 MiB
    )
    okHttpClient = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .cache(cache)
        .apply {
            addGenericDns(
                "https://dns.google/dns-query",
                listOf(
                    "8.8.4.4",
                    "8.8.8.8"
                )
            )
        }
        .build()
    client = Requests(
        okHttpClient,
        defaultHeaders,
        defaultCacheTime = 6,
        defaultCacheTimeUnit = TimeUnit.HOURS,
        responseParser = Mapper
    )
}

object Mapper : ResponseParser {

    @OptIn(ExperimentalSerializationApi::class)
    val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @OptIn(InternalSerializationApi::class)
    override fun <T : Any> parse(text: String, kClass: KClass<T>): T {
        return json.decodeFromString(kClass.serializer(), text)
    }

    override fun <T : Any> parseSafe(text: String, kClass: KClass<T>): T? {
        return try {
            parse(text, kClass)
        } catch (e: Exception) {
            null
        }
    }

    inline fun <reified T> parse(text: String): T {
        return json.decodeFromString(text)
    }
}

class Constants {

    companion object {


        // Network Requests Header
        const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36"

        const val ORIGIN = "https://gogoanime.gg/"
        const val REFERER = "https://goload.pro/"
        const val JIKAN_API_URL = "https://api.jikan.moe"
        const val IMAGE_URL = "https://s4.anilist.co/file/anilistcdn/character/medium/2102.jpg"
        const val ANILIST_API_URL = "https://graphql.anilist.co"

        fun showSnack(view: View?, message: String?) {
            view?.let {
                val snack =
                    Snackbar.make(view, message ?: "Error Occurred", Snackbar.LENGTH_LONG)
                if (!snack.isShown) {
                    snack.show()
                }
            }
        }

        fun getNetworkHeader(): Map<String, String> {
            return mapOf("referer" to REFERER, "origin" to ORIGIN, "user-agent" to USER_AGENT)
        }
    }

}

