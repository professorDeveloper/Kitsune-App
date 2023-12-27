/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

@file:OptIn(FlowPreview::class)

package com.azamovhudstc.graphqlanilist.ui.screens.detail

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.azamovhudstc.graphqlanilist.R
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.data.model.ui_models.Media
import com.azamovhudstc.graphqlanilist.databinding.DetailScreenBinding
import com.azamovhudstc.graphqlanilist.ui.screens.detail.adapter.TabAdapter
import com.azamovhudstc.graphqlanilist.utils.*
import com.azamovhudstc.graphqlanilist.viewmodel.DetailsViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestOptions
import com.draggable.library.extension.ImageViewerHelper
import com.flaviofaria.kenburnsview.RandomTransitionGenerator
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.lang.Math.abs
import kotlin.properties.Delegates

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DetailScreen : Fragment(R.layout.detail_screen), AppBarLayout.OnOffsetChangedListener {

    private lateinit var media: Media
    private val viewModel: DetailsViewModel by viewModels()

    private val animeDetails get() = requireArguments().getSerializable("data") as AniListMedia
    private var binding: DetailScreenBinding? = null
    private lateinit var adapter: TabAdapter
    private var check by Delegates.notNull<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = requireActivity().window
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val windowInsets =
            ViewCompat.getRootWindowInsets(window.decorView.findViewById(android.R.id.content))
        if (windowInsets != null) {
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DetailScreenBinding.bind(view)



        check = animeDetails.isFavourite
        binding!!.apply {
            viewModel.loadDataById(animeDetails.idAniList)
            viewModel.responseData.observe(viewLifecycleOwner) {
                when (it) {
                    is Result.Error -> {
                        progress.hide()
                        logError(it.exception)
                    }
                    Result.Loading -> {
                        mediaAppBar.hide()
                        viewPager.hide()
                        progress.show()

                    }
                    is Result.Success -> {
                        media = it.data
                        val media = it.data
                        binding!!.fabBack.setOnClickListener {
                            findNavController().popBackStack()
                        }
                        mediaAppBar.show()
                        viewPager.show()
                        progress.hide()
                        setTab(media, animeDetails)
                        binding!!.title.setText(media.title!!.userPreferred)
                        val adi = AccelerateDecelerateInterpolator()
                        val generator =
                            RandomTransitionGenerator((10000 + 15000 * (1f)).toLong(), adi)
                        Glide.with(context as Context)
                            .load(GlideUrl(media.bannerImage ?: media.coverImage!!.large))
                            .diskCacheStrategy(DiskCacheStrategy.ALL).override(400)
                            .apply(RequestOptions.bitmapTransform(BlurTransformation(2, 3)))
                            .into(itemCompactBannerNoKen)

                        itemCompactBannerNoKen.setTransitionGenerator(generator)
                        itemCompactImage.loadImage(media.coverImage!!.large)
                        var genresL = ""
                        media.genres.apply {
                            var count = 0
                            if (this != null) {
                                forEach {
                                    if (count <= 2) {

                                        count++
                                        genresL += "$it • "
                                    }
                                }
                                genresL = genresL.removeSuffix(" • ")
                            }

                            val genres =
                                "Format :${media.format!!.name} \n${genresL}"
                            itemDescription.text = genres
                        }
                    }
                }

            }


            itemCompactImage.setOnClickListener {
                ImageViewerHelper.showSimpleImage(
                    requireContext(),
                    ImageViewerHelper.ImageInfo(media.coverImage!!.large.toString()),
                    itemCompactBannerNoKen,
                    showDownLoadBtn = false
                )
            }
        }

        setAnimations()

    }



    private fun setAnimations() {
        var animationDuration: Long = 880
        binding?.cardView?.slideStart(animTime = animationDuration, 0)
        binding?.fabBack?.slideUp(animTime = animationDuration, 0)
        binding?.itemDescription?.slideStart(animTime = animationDuration, 0)
        binding?.playButtonForBanner?.slideUp(animTime = animationDuration, 0)
        binding?.title?.slideStart(animTime = animationDuration, 0)

        binding?.pageType?.slideStart(animTime = animationDuration, 0)
    }


    private fun setTab(data: Media, uiData: AniListMedia) {

        binding?.apply {

            mMaxScrollSize = binding!!.mediaAppBar.totalScrollRange
            binding!!.mediaAppBar.addOnOffsetChangedListener(this@DetailScreen)
            adapter = TabAdapter(media = data, uiData = uiData, requireActivity())
            viewPager.adapter = adapter
            viewPager.isUserInputEnabled = false
            TabLayoutMediator(pageType, viewPager) { _, _ ->

            }.attach()
            for (i in 0 until pageType.tabCount) {
                pageType.getTabAt(i)
                    ?.let { TooltipCompat.setTooltipText(it.view, null) }
            }
            val tabCount = pageType.tabCount
            for (i in 0 until tabCount) {
                val tab = pageType.getTabAt(i)
                tab!!.text = localLoadTabTxt()[i]
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        this.requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.statusBarColor)
        binding = null

    }

    private var isCollapsed = false
    private val percent = 50
    private var mMaxScrollSize = 0
    private var screenWidth: Float = 0f

    @SuppressLint("ResourceType")
    override fun onOffsetChanged(appBar: AppBarLayout?, i: Int) {
        if (mMaxScrollSize == 0) mMaxScrollSize = appBar!!.totalScrollRange
        val percentage = kotlin.math.abs(i) * 100 / mMaxScrollSize
        val cap = MathUtils.clamp((percent - percentage) / percent.toFloat(), 0f, 1f)

        binding!!.cardView.scaleX = 1f * cap
        binding!!.cardView.scaleY = 1f * cap
        binding!!.cardView.cardElevation = 32f * cap

        binding!!.cardView.visibility =
            if (binding!!.cardView.scaleX == 0f) View.GONE else View.VISIBLE

        if (percentage >= percent && !isCollapsed) {
            isCollapsed = true
            binding!!.itemCompactBannerNoKen.pause()
            this.requireActivity().window.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
        }
        if (percentage <= percent && isCollapsed) {
            isCollapsed = false
            binding!!.itemCompactBannerNoKen.resume()
            this.requireActivity().window.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.statusBarColor)

        }
    }

}