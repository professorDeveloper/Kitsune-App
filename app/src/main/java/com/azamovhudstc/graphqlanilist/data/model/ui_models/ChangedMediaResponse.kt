package com.azamovhudstc.graphqlanilist.data.model.ui_models

import android.os.Parcelable
import com.azamovhudstc.graphqlanilist.utils.MediaStatusAnimity
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChangedMediaResponse(
    val id: Int?,
    val status: MediaStatusAnimity?
) : Parcelable
