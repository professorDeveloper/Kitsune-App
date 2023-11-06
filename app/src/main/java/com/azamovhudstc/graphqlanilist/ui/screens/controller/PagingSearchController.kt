package com.azamovhudstc.graphqlanilist.ui.screens.controller

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging3.PagingDataEpoxyController
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.SearchLayoutBindingModel_
import com.azamovhudstc.graphqlanilist.utils.logError
import kotlinx.coroutines.ObsoleteCoroutinesApi

@OptIn(ObsoleteCoroutinesApi::class)
class PagingSearchController() :
    PagingDataEpoxyController<AniListMedia>() {
    /**
     * It creates a new EpoxyModel for each item in the list.
     *
     * @param currentPosition Int - The current position of the item in the list
     * @param item AniListMedia? - The item that is being bound to the view.
     * @return A EpoxyModel<*>
     */
    override fun buildItemModel(
        currentPosition: Int,
        item: AniListMedia?
    ): EpoxyModel<*> {
        return SearchLayoutBindingModel_().id(item?.idAniList).clickListener { view ->
            try {
                item?.let {

                    val params = mapOf(
                        "genre" to item.genres.firstOrNull()?.name
                    )
                }
            } catch (e: Exception) {
                logError(e)
            }
        }.animeInfo(item)
    }
}
