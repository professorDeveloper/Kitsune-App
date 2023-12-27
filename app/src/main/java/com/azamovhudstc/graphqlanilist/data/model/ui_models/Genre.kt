/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

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
