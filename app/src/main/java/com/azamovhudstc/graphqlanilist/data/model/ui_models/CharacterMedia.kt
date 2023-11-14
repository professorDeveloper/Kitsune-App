package com.azamovhudstc.graphqlanilist.data.model.ui_models

import com.azamovhudstc.graphqlanilist.CharacterDataByIDQuery
import java.io.Serializable

public data class CharacterMedia(
    /**
     * The id of the character
     */
    public val id: Int,
    /**
     * The character's age. Note this is a string, not an int, it may contain further text and
     * additional ages.
     */
    public val age: String?,
    /**
     * The character's gender. Usually Male, Female, or Non-binary but can be any string.
     */
    public val gender: String?,
    /**
     * A general description of the character
     */
    public val description: String?,
    /**
     * The character's birth date
     */
    public val dateOfBirth: CharacterDataByIDQuery.DateOfBirth?,
    /**
     * Media that includes the character
     */
    public val media: CharacterDataByIDQuery.Media?,
) : Serializable
