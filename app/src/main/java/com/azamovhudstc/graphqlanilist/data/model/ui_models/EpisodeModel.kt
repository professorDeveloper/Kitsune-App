package com.azamovhudstc.graphqlanilist.data.model.ui_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EpisodeModel(
    var episodeNumber: String,
    var episodeUrl: String,
    var episodeType: String,
    var percentage: Int = 0,
) : Parcelable