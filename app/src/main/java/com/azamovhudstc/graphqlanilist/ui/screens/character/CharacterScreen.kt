package com.azamovhudstc.graphqlanilist.ui.screens.character

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.azamovhudstc.graphqlanilist.R
import com.azamovhudstc.graphqlanilist.databinding.FragmentCharacterScreenBinding
import com.azamovhudstc.graphqlanilist.ui.screens.character.adapter.CharacterAdapter
import com.azamovhudstc.graphqlanilist.ui.screens.character.adapter.CharacterItemAdapter
import com.azamovhudstc.graphqlanilist.utils.*
import com.azamovhudstc.graphqlanilist.utils.Constants.Companion.IMAGE_URL
import com.azamovhudstc.graphqlanilist.viewmodel.CharacterViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestOptions
import com.flaviofaria.kenburnsview.RandomTransitionGenerator
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlin.math.abs

@AndroidEntryPoint
class CharacterScreen : Fragment(), AppBarLayout.OnOffsetChangedListener {
    private var _binding: FragmentCharacterScreenBinding? = null
    private val binding get() = _binding!!
    private val model by viewModels<CharacterViewModel>()
    private var statusBarHeight = 0
    private val characterID get() = requireArguments().getInt("id", 0)
    private val coverImage get() = requireArguments().getString("coverImage", Constants.IMAGE_URL)
    private val bannerImage get() = requireArguments().getString("bannerImage", Constants.IMAGE_URL)
    private val characterName
        get() = requireArguments().getString(
            "characterName",
            Constants.IMAGE_URL
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val windowInsets =
            ViewCompat.getRootWindowInsets(requireActivity().window.decorView.findViewById(android.R.id.content))
        if (windowInsets != null) {
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            statusBarHeight = insets.top
        }
        screenWidth = resources.displayMetrics.run { widthPixels / density }

        model.loadData(characterID)
        binding.characterAppBar.addOnOffsetChangedListener(this)
        val banner = binding.characterBanner
        banner.updateLayoutParams { height += statusBarHeight }
        binding.characterClose.updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin += statusBarHeight }
        binding.characterCollapsing.minimumHeight = statusBarHeight
        binding.characterCover.updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin += statusBarHeight }
        binding.characterTitle.isSelected = true

        model.characterData.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Error -> {

                }
                Result.Loading -> {
                    binding.apply {
                        characterAppBar.hide()
                        characterCover.hide()
                        characterProgress.show()
                        characterRecyclerView.hide()
                    }
                }
                is Result.Success -> {
                    binding.apply {
                        characterAppBar.show()
                        characterProgress.hide()
                        characterCover.show()
                        characterRecyclerView.show()

                        val character = it.data
                        val adi = AccelerateDecelerateInterpolator()
                        val generator =
                            RandomTransitionGenerator((10000 + 15000 * (1f)).toLong(), adi)
                        Glide.with(context as Context)
                            .load(
                                GlideUrl(
                                    bannerImage ?: IMAGE_URL
                                )
                            )
                            .diskCacheStrategy(DiskCacheStrategy.ALL).override(400)
                            .apply(RequestOptions.bitmapTransform(BlurTransformation(2, 3)))
                            .into(banner)
                        banner.setTransitionGenerator(generator)
                        binding.characterCoverImage.loadImage(coverImage)
                        binding.characterTitle.text = characterName
                        if (character.media?.edges != null) {
                            val mediaAdaptor = CharacterItemAdapter(character.media.edges)
                            val concatAdaptor =
                                ConcatAdapter(
                                    CharacterAdapter(character, requireActivity()),
                                    mediaAdaptor
                                )
                            val gridSize = (screenWidth / 124f).toInt()
                            val gridLayoutManager = GridLayoutManager(requireContext(), gridSize)
                            gridLayoutManager.spanSizeLookup =
                                object : GridLayoutManager.SpanSizeLookup() {
                                    override fun getSpanSize(position: Int): Int {
                                        return when (position) {
                                            0 -> gridSize
                                            else -> 1
                                        }
                                    }
                                }
                            binding.characterRecyclerView.adapter = concatAdaptor
                            binding.characterRecyclerView.layoutManager = gridLayoutManager
                        }



                    }
                }
            }
        }

    }


    private var isCollapsed = false
    private val percent = 30
    private var mMaxScrollSize = 0
    private var screenWidth: Float = 0f

    override fun onOffsetChanged(appBar: AppBarLayout, i: Int) {
        if (mMaxScrollSize == 0) mMaxScrollSize = appBar.totalScrollRange
        val percentage = abs(i) * 100 / mMaxScrollSize
        val cap = MathUtils.clamp((percent - percentage) / percent.toFloat(), 0f, 1f)

        binding.characterCover.scaleX = 1f * cap
        binding.characterCover.scaleY = 1f * cap
        binding.characterCover.cardElevation = 32f * cap

        binding.characterCover.visibility =
            if (binding.characterCover.scaleX == 0f) View.GONE else View.VISIBLE

        if (percentage >= percent && !isCollapsed) {
            isCollapsed = true
            binding.characterAppBar.setBackgroundResource(R.color.colorPrimary)
        }
        if (percentage <= percent && isCollapsed) {
            isCollapsed = false
            binding.characterAppBar.setBackgroundResource(R.color.colorPrimaryDark)
        }
    }
}