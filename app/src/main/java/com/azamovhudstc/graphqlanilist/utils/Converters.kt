package com.azamovhudstc.graphqlanilist.utils

import android.text.format.DateUtils
import com.apollographql.apollo3.api.ApolloResponse
import com.azamovhudstc.graphqlanilist.FavoritesAnimeQuery
import com.azamovhudstc.graphqlanilist.SaveMediaMutation
import com.azamovhudstc.graphqlanilist.data.mapper.convert
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.data.model.ui_models.ChangedMediaResponse
import java.text.ParseException
import java.util.*

fun ApolloResponse<SaveMediaMutation.Data>.convert(): ChangedMediaResponse {
    return ChangedMediaResponse(
        this.data?.saveMedia?.id,
        MediaStatusAnimity.stringToMediaListStatus(this.data?.saveMedia?.status?.rawValue)
    )
}
fun ApolloResponse<FavoritesAnimeQuery.Data>.convert(): List<AniListMedia> {
    return this.data
        ?.user
        ?.favourites
        ?.anime
        ?.edges
        ?.mapNotNull {
            it?.node
                ?.homeMedia
                ?.convert()
        } ?: emptyList()
}
fun Int.parseTime(errorHappened: () -> Unit): CharSequence? {
    return try {
        val now = System.currentTimeMillis()
        DateUtils.getRelativeTimeSpanString(now, toLong(), DateUtils.MINUTE_IN_MILLIS)
    } catch (e: ParseException) {
        e.printStackTrace()
        errorHappened()
        ""
    }
}

enum class MediaStatusAnimity {
    COMPLETED,
    WATCHING,
    DROPPED,
    PAUSED,
    PLANNING,
    REPEATING,
    NOTHING;

    companion object {
        fun stringToMediaListStatus(passedString: String?): MediaStatusAnimity {
            return when (passedString?.uppercase(Locale.getDefault())) {
                "COMPLETED" -> COMPLETED
                "CURRENT" -> WATCHING
                "DROPPED" -> DROPPED
                "PAUSED" -> PAUSED
                "PLANNING" -> PLANNING
                "REPEATING" -> REPEATING
                else -> NOTHING
            }
        }
    }
}
