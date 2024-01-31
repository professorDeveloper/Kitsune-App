/*
 *  Created by Azamov X ㋡ on 2/1/24, 12:23 AM
 *  Copyright (c) 2024 . All rights reserved.
 *  Last modified 2/1/24, 12:22 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.tv

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.Presenter
import androidx.core.content.ContextCompat
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.leanback.widget.PresenterSelector
import androidx.lifecycle.lifecycleScope
import com.azamovhudstc.graphqlanilist.tv.components.ButtonListRow
import com.azamovhudstc.graphqlanilist.R
import com.azamovhudstc.graphqlanilist.tv.presenters.ButtonListRowPresenter
import com.azamovhudstc.graphqlanilist.tv.presenters.MainHeaderPresenter
import com.azamovhudstc.graphqlanilist.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable

/**
 * Loads a grid of cards with movies to browse.
 */
@AndroidEntryPoint

class MainFragment : BrowseSupportFragment() {
    companion object {
        var shouldReload: Boolean = false
    }

    private val PAGING_THRESHOLD = 15

    private val model: SearchViewModel by activityViewModels()


    //TODO Sketchy handling here
    var nCallbacks: Int = 0

    lateinit var continueAdapter: ArrayObjectAdapter
    lateinit var recommendedAdapter: ArrayObjectAdapter
    lateinit var plannedAdapter: ArrayObjectAdapter
    lateinit var completedAdapter: ArrayObjectAdapter
    lateinit var genresAdapter: ArrayObjectAdapter
    lateinit var trendingAdapter: ArrayObjectAdapter
    lateinit var updatedAdapter: ArrayObjectAdapter
    lateinit var popularAdapter: ArrayObjectAdapter
    lateinit var rowAdapter: ArrayObjectAdapter

    lateinit var continueRow: ListRow
    lateinit var recommendedRow: ListRow
    lateinit var plannedRow: ListRow
    lateinit var completedRow: ListRow
    lateinit var genresRow: ListRow
    lateinit var trendingRow: ListRow
    lateinit var updatedRow: ListRow
    lateinit var popularRow: ListRow
    var loading: Boolean = false
    var viewLoaded: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUIElements()
    }

    private fun setupUIElements() {
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // Set fastLane (or headers) background color
        brandColor =
            Color.parseColor("#66000000")//ContextCompat.getColor(requireActivity(), R.color.bg_black)
        // Set search icon color.
        searchAffordanceColor = ContextCompat.getColor(requireActivity(), R.color.colorPrimary)

        setHeaderPresenterSelector(object : PresenterSelector() {
            override fun getPresenter(o: Any): Presenter {
                if (o is ButtonListRow) {
                    return ButtonListRowPresenter()
                } else {
                    return MainHeaderPresenter()
                }
            }
        })

        setOnSearchClickedListener {
//            val fragment = TVSearchFragment()
//            fragment.setArgs("ANIME", null, null)
//            parentFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.main_tv_fragment, fragment).commit()
//        }

            setOnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->

//            if (item is Media) {
//                startActivity(
//                    Intent(
//                        requireContext(),
//                        TVDetailActivity::class.java
//                    ).putExtra("media", item as Serializable))
//            } else if (item is Pair<*,*>) {
//                val fragment = TVSearchFragment()
//                val genre = item.first as String
//                fragment.setArgs("ANIME", genre, "Trending", genre.lowercase() == "hentai")
//                parentFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.main_tv_fragment, fragment).commit()
//            }
            }

            setOnItemViewSelectedListener { itemViewHolder, item, rowViewHolder, row ->
//            item?.let {
//                if ((row as ListRow).adapter == popularAdapter && model.searchResults.hasNextPage && model.searchResults.results.isNotEmpty() && !loading && isNearEndOfList(popularAdapter, item)) {
//                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
//                        loading=true
//                        model.loadNextPage(model.searchResults)
//                    }
//                }
//                //TODO find a way to properly show this image
//                /*if (it is Media && !it.banner.isNullOrEmpty()) {
//                    Glide.with(requireContext())
//                        .asDrawable()
//                        .centerCrop()
//                        .load(it.banner)
//                        .into(object : CustomTarget<Drawable>() {
//                            override fun onResourceReady(
//                                resource: Drawable,
//                                transition: Transition<in Drawable>?
//                            ) {
//                                backgroundManager.drawable = resource
//                            }
//
//                            override fun onLoadCleared(placeholder: Drawable?) {}
//                        })
//                } else {
//                    //backgroundManager.clearDrawable()
//                }*/
//            }
            }
        }
    }

}