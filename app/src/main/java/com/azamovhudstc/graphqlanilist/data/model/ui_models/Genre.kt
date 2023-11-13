package com.azamovhudstc.graphqlanilist.data.model.ui_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Genre(
    val name: String = ""
) : Parcelable

@Parcelize
data class GenreByImage(
    val name: String = "",
    val image: String = "",
    val time: Long = 0,
    val id: Int = 0
) : Parcelable
