/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.data.network.rest.jikan

data class Data(
    val episode: String,
    val images: Images?,
    val mal_id: Int,
    val title: String,
    val url: String
)