package com.azamovhudstc.graphqlanilist.data.model.ui_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EpisodeEntity(
   var episodeUrl: String = "",
   var malId: Int = 0,
   var watchedDuration: Long = 0,
   var duration: Long = 0
) : Parcelable {
    fun getWatchedPercentage(): Int = ((watchedDuration * 100) / duration).toInt()
}
