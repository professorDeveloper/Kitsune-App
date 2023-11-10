package com.azamovhudstc.graphqlanilist.data.local

import com.azamovhudstc.graphqlanilist.data.model.ui_models.EpisodeModel

abstract class BaseParser {
    open val name: String = ""
    abstract suspend fun fetchEpisodeList(response: String): List<EpisodeModel>
}
