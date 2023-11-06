package com.azamovhudstc.graphqlanilist.data.mapper

import com.azamovhudstc.graphqlanilist.SearchAnimeQuery
import com.azamovhudstc.graphqlanilist.data.model.ui_models.*
import com.azamovhudstc.graphqlanilist.fragment.HomeMedia

fun SearchAnimeQuery.Data.convert(): List<AniListMedia> {
    return page?.media?.mapNotNull {
        it?.homeMedia.convert()
    } ?: emptyList()
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
