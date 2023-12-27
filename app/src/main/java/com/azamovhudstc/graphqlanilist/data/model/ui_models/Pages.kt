/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.data.model.ui_models

import com.azamovhudstc.graphqlanilist.GetGenersByThumblainQuery

public data class Pages(
    public val media: List<GetGenersByThumblainQuery.Medium?>?,
)
