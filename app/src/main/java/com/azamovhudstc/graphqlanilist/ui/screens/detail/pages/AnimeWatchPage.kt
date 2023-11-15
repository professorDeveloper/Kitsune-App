package com.azamovhudstc.graphqlanilist.ui.screens.detail.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.data.model.ui_models.Media
import com.azamovhudstc.graphqlanilist.data.network.rest.jikan.Data
import com.azamovhudstc.graphqlanilist.databinding.FragmentAnimeWatchPageBinding
import com.azamovhudstc.graphqlanilist.ui.adapter.AnimeWatchAdapter
import com.azamovhudstc.graphqlanilist.ui.adapter.EpisodesAdapter
import com.azamovhudstc.graphqlanilist.utils.*
import com.azamovhudstc.graphqlanilist.viewmodel.AnimeWatchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.max
import kotlin.math.roundToInt

@AndroidEntryPoint
class AnimeWatchPage(private val aniListMedia: AniListMedia, private val media: Media) :
    Fragment() {
    private var end: Int? = null

    private var _binding: FragmentAnimeWatchPageBinding? = null
    private val binding get() = _binding!!
    private var isOnlyOne = false
    private val model by viewModels<AnimeWatchViewModel>()
    private lateinit var headerAdapter: AnimeWatchAdapter
    private lateinit var episodeAdapter: EpisodesAdapter
    private lateinit var list: ArrayList<Data>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAnimeWatchPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        headerAdapter = AnimeWatchAdapter(this)
        episodeAdapter = EpisodesAdapter(emptyList())


        headerAdapter.setItemClickListener {
            model.loadEpisodesById(media.idMal!!.toInt())
        }
        var screenWidth = resources.displayMetrics.widthPixels.dp

        var maxGridSize = (screenWidth / 100f).roundToInt()
        maxGridSize = max(4, maxGridSize - (maxGridSize % 2))
        val gridLayoutManager = GridLayoutManager(requireContext(), maxGridSize)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val style = episodeAdapter.getItemViewType(position)

                return when (position) {
                    0 -> maxGridSize
                    else -> when (style) {
                        0 -> maxGridSize
                        1 -> 2
                        2 -> 1
                        else -> maxGridSize
                    }
                }
            }
        }

        binding.animeSourceRecycler.layoutManager = gridLayoutManager

        binding.animeSourceRecycler.adapter = ConcatAdapter(headerAdapter, episodeAdapter)

        model.episodeListLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Error -> {
                    binding.animeSourceRecycler.show()
                    binding.animeNotSupported.show()
                    binding.mediaInfoProgressBar.hide()

                }
                Result.Loading -> {
                    binding.animeSourceRecycler.hide()
                    binding.mediaInfoProgressBar.show()
                }
                is Result.Success -> {
                    binding.animeSourceRecycler.show()
                    binding.mediaInfoProgressBar.hide()
                    binding.apply {
                        list = it.data.data as ArrayList<Data>
                        episodeAdapter.list = it.data.data
                        episodeAdapter.list = list.reversed()
                        episodeAdapter.notifyDataSetChanged()
                        end = list.size - 1
                    }
                    logMessage(it.data.data.toString())
                }
            }
        }
    }

    fun onIconPressed(reversed: Boolean) {
        episodeAdapter.list = episodeAdapter.list.reversed()
        episodeAdapter.notifyDataSetChanged()
    }

}