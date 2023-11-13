package com.azamovhudstc.graphqlanilist.utils

import android.text.format.DateUtils
import com.azamovhudstc.graphqlanilist.DetailFullDataQuery
import com.azamovhudstc.graphqlanilist.data.model.ui_models.Media
import java.text.ParseException
import java.util.*



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
