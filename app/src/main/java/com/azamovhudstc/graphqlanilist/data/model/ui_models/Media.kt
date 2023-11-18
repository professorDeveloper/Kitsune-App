package com.azamovhudstc.graphqlanilist.data.model.ui_models

import com.azamovhudstc.graphqlanilist.DetailFullDataQuery
import com.azamovhudstc.graphqlanilist.type.MediaFormat
import com.azamovhudstc.graphqlanilist.type.MediaSeason
import com.azamovhudstc.graphqlanilist.type.MediaSource
import java.io.Serializable

public data class Media(
    /**
     * The official titles of the media in various languages
     */
    public val title: DetailFullDataQuery.Title?,
    /**
     * The banner image of the media
     */
    public val bannerImage: String?,
    /**
     * The cover images of the media
     */
    public val coverImage: DetailFullDataQuery.CoverImage?,
    /**
     * A weighted average score of all the user's scores of the media
     */
    public val averageScore: Int?,

    /**
     * Mean score of all the user's scores of the media
     */
    public val meanScore: Int?,

    /**
     * Official Twitter hashtags for the media
     */
    public val hashtag: String?,
    /**
     * The ranking of the media in a particular time span and format compared to other media
     */
    public val rankings: List<DetailFullDataQuery.Ranking?>?,
    /**
     * The id of the media
     */
    public val id: Int,
    /**
     * The authenticated user's media list entry for the media
     */
    public val mediaListEntry: DetailFullDataQuery.MediaListEntry?,
    /**
     * If the media is marked as favourite by the current authenticated user
     */
    public val isFavourite: Boolean,
    /**
     * The url for the media page on the AniList website
     */
    public val siteUrl: String?,
    /**
     * The mal id of the media
     */
    public val idMal: Int?,
    /**
     * The media's next episode airing schedule
     */
    public val nextAiringEpisode: DetailFullDataQuery.NextAiringEpisode?,
    /**
     * Source type the media was adapted from.
     */
    public val source: MediaSource?,
    /**
     * Where the media was created. (ISO 3166-1 alpha-2)
     */
    public val countryOfOrigin: Any?,
    /**
     * The format the media was released in
     */
    public val format: MediaFormat?,
    /**
     * The general length of each anime episode in minutes
     */
    public val duration: Int?,
    /**
     * The season the media was initially released in
     */
    public val season: MediaSeason?,
    /**
     * The season year the media was initially released in
     */
    public val seasonYear: Int?,
    /**
     * The first official release date of the media
     */
    public val startDate: DetailFullDataQuery.StartDate?,
    /**
     * The last official release date of the media
     */
    public val endDate: DetailFullDataQuery.EndDate?,
    /**
     * The genres of the media
     */
    public val genres: List<String?>?,
    /**
     * The companies who produced the media
     */
    public val studios: DetailFullDataQuery.Studios?,
    /**
     * Short description of the media's story and characters
     */
    public val description: String?,
    /**
     * Media trailer or advertisement
     */
    public val trailer: DetailFullDataQuery.Trailer?,
    /**
     * Alternative titles of the media
     */
    public val synonyms: List<String?>?,
    /**
     * List of tags that describes elements and themes of the media
     */
    public val tags: List<DetailFullDataQuery.Tag?>?,
    /**
     * The characters in the media
     */
    public val characters: DetailFullDataQuery.Characters?,
    /**
     * Other media in the same or connecting franchise
     */
    public val relations: DetailFullDataQuery.Relations?,
    /**
     * The staff who produced the media
     */
    public val staffPreview: DetailFullDataQuery.StaffPreview?,
    /**
     * User recommendations for similar media
     */
    public val recommendations: DetailFullDataQuery.Recommendations?,
    /**
     * External links to another site related to the media
     */
    public val externalLinks: List<DetailFullDataQuery.ExternalLink?>?,
) :Serializable{

}