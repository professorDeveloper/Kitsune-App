package com.azamovhudstc.graphqlanilist.ui.screens.detail.pages

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.azamovhudstc.graphqlanilist.R
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.data.model.ui_models.Media
import com.azamovhudstc.graphqlanilist.databinding.*
import com.azamovhudstc.graphqlanilist.ui.adapter.CharacterAdapter
import com.azamovhudstc.graphqlanilist.ui.adapter.GenreAdapter
import com.azamovhudstc.graphqlanilist.ui.adapter.MediaAdaptor
import com.azamovhudstc.graphqlanilist.ui.adapter.RecommendationAdapter
import com.azamovhudstc.graphqlanilist.utils.*
import com.azamovhudstc.graphqlanilist.utils.Constants.Companion.IMAGE_URL
import com.azamovhudstc.graphqlanilist.viewmodel.GenresViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnimeInfoPage(private val aniListMedia: AniListMedia, private val media: Media) : Fragment() {

    private val genreModel by viewModels<GenresViewModel>()
    private var _binding: FragmentAnimeInfoPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimeInfoPageBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mediaInfoContainer.visibility = View.VISIBLE
        if (media.title!!.userPreferred != null) binding.mediaInfoNameRomajiContainer.visibility =
            View.VISIBLE
        binding.mediaInfoMeanScore.text =
            if (media.meanScore != null) (media.meanScore / 10.0).toString() else "??"
        binding.mediaInfoStatus.text = aniListMedia.status?.name ?: "UNKNOWN"
        binding.mediaInfoFormat.text = media.format?.name ?: "??"
        binding.mediaInfoSource.text = media.source?.name ?: "??"
        binding.mediaInfoTotal.text = "${media.nextAiringEpisode?.episode ?: "??"}"
//        binding.hashTag.text =media.hashtag!!
        binding.mediaInfoStart.text = media.startDate?.toSortString() ?: "??"
        binding.mediaInfoEnd.text = media.endDate?.toSortString() ?: "??"
        binding.mediaInfoDuration.text =
            if (media.duration != null) media.duration.toString() else "??"
        binding.mediaInfoDurationContainer.visibility = View.VISIBLE
        binding.mediaInfoSeasonContainer.visibility = View.VISIBLE
        binding.mediaInfoSeason.text =
            (media.season?.name ?: "??") + " " + (media.seasonYear ?: "??")
        if (media.studios?.nodes != null) {
            if (media.studios.nodes.isNotEmpty()) {
                binding.mediaInfoStudioContainer.visibility = View.VISIBLE
                binding.mediaInfoStudio.text = media.studios.nodes!!.get(0)!!.name
                binding.mediaInfoStudioContainer.setOnClickListener {
                }
            }


        }
        val desc = HtmlCompat.fromHtml(
            (media.description ?: "null").replace("\\n", "<br>").replace("\\\"", "\""),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.mediaInfoDescription.text =
            "\t\t\t" + if (desc.toString() != "null") desc else "No Description Available"
        binding.mediaInfoDescription.setOnClickListener {
            if (binding.mediaInfoDescription.maxLines == 5) {
                ObjectAnimator.ofInt(binding.mediaInfoDescription, "maxLines", 100)
                    .setDuration(950).start()
            } else {
                ObjectAnimator.ofInt(binding.mediaInfoDescription, "maxLines", 5)
                    .setDuration(400).start()
            }
        }
        binding.mediaInfoTotalTitle.setText(R.string.total_eps)
        val parent = _binding?.mediaInfoContainer!!
        val screenWidth = resources.displayMetrics.run { widthPixels / density }




        if (media.synonyms!!.isNotEmpty()) {
            val bind = ItemTitleChipgroupBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            for (position in media.synonyms.indices) {
                val chip = ItemChipBinding.inflate(
                    LayoutInflater.from(context),
                    bind.itemChipGroup,
                    false
                ).root
                chip.text = media.synonyms[position]
//                chip.setOnLongClickListener { copyToClipboard(media.synonyms[position]);true }
                bind.itemChipGroup.addView(chip)
            }
            parent.addView(bind.root)
        }
        if (media.trailer != null) {
            @Suppress("DEPRECATION")
            class MyChrome : WebChromeClient() {
                private var mCustomView: View? = null
                private var mCustomViewCallback: CustomViewCallback? = null
                private var mOriginalSystemUiVisibility = 0

                override fun onHideCustomView() {
                    (requireActivity().window.decorView as FrameLayout).removeView(
                        mCustomView
                    )
                    mCustomView = null
                    requireActivity().window.decorView.systemUiVisibility =
                        mOriginalSystemUiVisibility
                    mCustomViewCallback!!.onCustomViewHidden()
                    mCustomViewCallback = null
                }

                override fun onShowCustomView(
                    paramView: View,
                    paramCustomViewCallback: CustomViewCallback
                ) {
                    if (mCustomView != null) {
                        onHideCustomView()
                        return
                    }
                    mCustomView = paramView
                    mOriginalSystemUiVisibility =
                        requireActivity().window.decorView.systemUiVisibility
                    mCustomViewCallback = paramCustomViewCallback
                    (requireActivity().window.decorView as FrameLayout).addView(
                        mCustomView,
                        FrameLayout.LayoutParams(-1, -1)
                    )
                    requireActivity().window.decorView.systemUiVisibility =
                        3846 or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                }
            }

            val bind = ItemTitleTrailerBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            bind.mediaInfoTrailer.apply {
                visibility = View.VISIBLE
                settings.javaScriptEnabled = true
                isSoundEffectsEnabled = true
                webChromeClient = MyChrome()
                loadUrl(media.trailer.getYoutubeFormat())
            }
            parent.addView(bind.root)
        }



        if (media.genres!!.isNotEmpty()) {
            val bind = ActivityGenreBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            val listorNull = media.genres ?: emptyList<String?>()
            genreModel.loadGenres()
            genreModel.genres.observe(viewLifecycleOwner) {
                val adapter = GenreAdapter("ANIME", listorNull, it)
                bind.mediaInfoGenresRecyclerView.adapter = adapter
                bind.mediaInfoGenresRecyclerView.layoutManager =
                    GridLayoutManager(requireActivity(), 1.toInt(), HORIZONTAL, false)

                bind.mediaInfoGenresRecyclerView.adapter = adapter
                parent.addView(bind.root)

                if (media.tags!!.isNotEmpty()) {
                    val bind = ItemTitleChipgroupBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false
                    )
                    bind.itemTitle.setText("Tags")
                    for (position in media.tags.indices) {
                        val chip = ItemChipBinding.inflate(
                            LayoutInflater.from(context),
                            bind.itemChipGroup,
                            false
                        ).root
                        chip.setTextColor(Color.WHITE)
                        chip.chipBackgroundColor = ColorStateList.valueOf(randomColor())
                        val data = media.tags[position]
                        chip.text = "${data?.name} : ${if (data!!.rank != null) "${data.rank}%" else ""}"
                        chip.setOnClickListener {
                        }
                        bind.itemChipGroup.addView(chip)
                    }
                    parent.addView(bind.root)

                }
                if (media.characters != null) {
                    if (media.characters.edges!!.isNotEmpty()) {
                        val bind = ItemTitleRecyclerBinding.inflate(
                            LayoutInflater.from(context),
                            parent,
                            false
                        )
                        bind.itemTitle.setText(R.string.characters)
                        var adapter =
                            CharacterAdapter(media.characters.edges.toMutableList())
                        bind.itemRecycler.adapter = adapter
                        adapter.setItemClickListener { data, title, position ->
                            var bundle = Bundle()

                            bundle.putInt("id", data.node?.id ?: 0)
                            bundle.putString("coverImage", data.node?.image?.medium ?: IMAGE_URL)
                            bundle.putString("characterName", data.node?.name?.userPreferred.toString())
                            bundle.putString(
                                "bannerImage",
                                media.bannerImage ?: media.coverImage?.large ?: IMAGE_URL
                            )
                            findNavController().navigate(R.id.characterScreen, bundle,
                                animationTransaction().build())

                        }
                        bind.itemRecycler.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        parent.addView(bind.root)
                    }
                }
                if (media.relations?.edges != null) {
                    if (media.relations.edges.isNotEmpty()) {
                        val bind = ItemTitleRecyclerBinding.inflate(
                            LayoutInflater.from(context),
                            parent,
                            false
                        )
                        bind.itemTitle.setText(R.string.relations)
                        val adapter = MediaAdaptor(list = media.relations.edges!!.toMutableList())
                        bind.itemRecycler.adapter = adapter
                        adapter.setItemClickListener {
                            val bundle = Bundle()
                            bundle.putSerializable("data", AniListMedia(it.node!!.id, it.node!!.idMal))
                            findNavController().navigate(
                                R.id.detailScreen,
                                bundle,
                                animationTransactionClearStack(R.id.detailScreen).build()
                            )
                        }
                        bind.itemRecycler.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )

                        parent.addView(bind.root)
                    }
                }
                if (media.recommendations?.nodes != null) {
                    if (media.recommendations.nodes.isNotEmpty()) {
                        val bind = ItemTitleRecyclerBinding.inflate(
                            LayoutInflater.from(context),
                            parent,
                            false
                        )
                        bind.itemTitle.setText("Recommendation")
                        val adapter = RecommendationAdapter(list = media.recommendations.nodes)
                        bind.itemRecycler.adapter=adapter
                        bind.itemRecycler.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        adapter.setItemClickListener {
                            val bundle = Bundle()
                            bundle.putSerializable("data", AniListMedia(it.mediaRecommendation!!.id, it.mediaRecommendation!!.idMal))
                            findNavController().navigate(
                                R.id.detailScreen,
                                bundle,
                                animationTransactionClearStack(R.id.detailScreen).build())

                        }
                        parent.addView(bind.root)
                    }
                }
            }

        }


    }

}