/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.data.model
data class AnimeStreamLink(
    val link: String,
    val subsLink: String,
    val isHls: Boolean,
    val extraHeaders: HashMap<String, String>? = null
)
