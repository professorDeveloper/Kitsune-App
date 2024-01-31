/*
 *  Created by Azamov X ㋡ on 2/1/24, 12:23 AM
 *  Copyright (c) 2024 . All rights reserved.
 *  Last modified 2/1/24, 12:23 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.tv

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter

class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(
        viewHolder: AbstractDetailsDescriptionPresenter.ViewHolder,
        item: Any
    ) {
        val movie = item as Movie

        viewHolder.title.text = movie.title
        viewHolder.subtitle.text = movie.studio
        viewHolder.body.text = movie.description
    }
}