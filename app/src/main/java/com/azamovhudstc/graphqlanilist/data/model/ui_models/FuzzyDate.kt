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
data class FuzzyDate(
    val year: Int? = null,
    val month: Int? = null,
    val day: Int? = null
) : Parcelable {
    private fun isNull(): Boolean {
        return year == null || month == null || day == null
    }

    fun getDate(): String {
        return if (!isNull()) {
            "$year/$month/$day"
        } else {
            "Unknown"
        }
    }
}
