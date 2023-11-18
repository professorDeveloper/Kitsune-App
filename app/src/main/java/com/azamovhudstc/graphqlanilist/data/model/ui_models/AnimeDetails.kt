package com.azamovhudstc.graphqlanilist.data.model.ui_models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class AnimePlayingDetails(
    val animeName: String,
    val animeUrl: String,
    var animeEpisodeIndex: String,
    val animeEpisodeMap: HashMap<String, String>,
    val animeTotalEpisode: String,
    val epType: String
):Serializable