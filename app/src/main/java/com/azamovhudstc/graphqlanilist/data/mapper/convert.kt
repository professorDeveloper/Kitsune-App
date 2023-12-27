/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.data.mapper

import com.azamovhudstc.graphqlanilist.DetailFullDataQuery
import com.azamovhudstc.graphqlanilist.GetGenersByThumblainQuery
import com.azamovhudstc.graphqlanilist.SearchAnimeQuery
import com.azamovhudstc.graphqlanilist.data.model.ui_models.*
import com.azamovhudstc.graphqlanilist.fragment.HomeMedia


fun DetailFullDataQuery.Data.convert(): Media {
    return this.media!!.convert()
}
fun GetGenersByThumblainQuery.Data.convert(): Pages {
    return Pages(this.Page?.media)
}



fun SearchAnimeQuery.Data.convert(): List<AniListMedia> {
    return page?.media?.mapNotNull {
        it?.homeMedia.convert()
    } ?: emptyList()
}

fun DetailFullDataQuery.Media.convert(): Media {
    return Media(
        this.title,
        this.bannerImage,
        this.coverImage,
        this.averageScore,
        this.meanScore,
        this.hashtag,
        this.rankings,
        this.id,
        this.mediaListEntry,
        this.isFavourite,
        this.siteUrl,
        this.idMal,
        this.nextAiringEpisode,
        this.source,
        this.countryOfOrigin,
        this.format,
        this.duration,
        this.season,
        this.seasonYear,
        this.startDate,
        this.endDate,
        this.genres,
        this.studios,
        this.description,
        this.trailer,
        this.synonyms,
        this.tags,
        this.characters,
        this.relations,
        this.staffPreview,
        this.recommendations,
        this.externalLinks
    )
}

fun HomeMedia?.convert(): AniListMedia {
    return AniListMedia(
        idAniList = this?.id ?: 0,
        idMal = this?.idMal,
        title = MediaTitle(userPreferred = this?.title?.userPreferred.orEmpty()),
        type = this?.type,
        format = this?.format,
        isFavourite = this?.isFavourite ?: false,
        streamingEpisode = this?.streamingEpisodes?.mapNotNull { it.convert() },
        nextAiringEpisode = this?.nextAiringEpisode?.airingAt,
        status = this?.status,
        description = this?.description.orEmpty(),
        startDate = if (this?.startDate?.year != null) {
            FuzzyDate(this.startDate.year, this.startDate.month, this.startDate.day)
        } else {
            null
        },
        coverImage = MediaCoverImage(
            this?.coverImage?.extraLarge.orEmpty(),
            this?.coverImage?.large.orEmpty(),
            this?.coverImage?.medium.orEmpty()
        ),
        bannerImage = this?.bannerImage.orEmpty(),
        genres = this?.genres?.mapNotNull { Genre(name = it.orEmpty()) } ?: emptyList(),
        averageScore = this?.averageScore ?: 0,
        favourites = this?.favourites ?: 0,
        mediaListEntry = MediaStatusAnimity.stringToMediaListStatus(this?.mediaListEntry?.status?.rawValue)
    )
}


fun HomeMedia.StreamingEpisode?.convert() =
    Episodes(
        this?.title,
        this?.thumbnail
    )
