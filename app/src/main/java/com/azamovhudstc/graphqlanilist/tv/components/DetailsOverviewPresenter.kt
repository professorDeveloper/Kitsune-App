package com.azamovhudstc.graphqlanilist.tv.components

import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.leanback.widget.Presenter
import com.azamovhudstc.graphqlanilist.R


class DetailsOverviewPresenter(detailsPresenter: Presenter?): FullWidthDetailsOverviewRowPresenter(detailsPresenter) {

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_main_tv
    }
}