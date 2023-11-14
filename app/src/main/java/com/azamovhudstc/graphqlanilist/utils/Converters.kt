package com.azamovhudstc.graphqlanilist.utils

import android.text.format.DateUtils
import com.azamovhudstc.graphqlanilist.CharacterDataByIDQuery
import com.azamovhudstc.graphqlanilist.data.model.ui_models.CharacterMedia
import java.text.ParseException
import java.util.*


fun CharacterDataByIDQuery.Data.convert(): CharacterMedia {
    return CharacterMedia(
        this.Character!!.id,
        this.Character.age,
        this.Character.gender,
        this.Character.description,
        this.Character.dateOfBirth,
        this.Character.media
    )
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
