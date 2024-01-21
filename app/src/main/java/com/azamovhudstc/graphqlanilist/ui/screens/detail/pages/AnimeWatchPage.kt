/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.ui.screens.detail.pages

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AnimePlayingDetails
import com.azamovhudstc.graphqlanilist.data.network.rest.jikan.Data
import com.azamovhudstc.graphqlanilist.data.network.rest.jikan.Images
import com.azamovhudstc.graphqlanilist.data.network.rest.jikan.Jpg
import com.azamovhudstc.graphqlanilist.databinding.FragmentAnimeWatchPageBinding
import com.azamovhudstc.graphqlanilist.source.AnimeSource
import com.azamovhudstc.graphqlanilist.source.SourceSelector
import com.azamovhudstc.graphqlanilist.source.source_imp.AllAnimeSource
import com.azamovhudstc.graphqlanilist.source.source_imp.AniWaveSource
import com.azamovhudstc.graphqlanilist.source.source_imp.YugenSource
import com.azamovhudstc.graphqlanilist.ui.activity.PlayerActivity
import com.azamovhudstc.graphqlanilist.ui.adapter.AnimeWatchAdapter
import com.azamovhudstc.graphqlanilist.ui.adapter.EpisodesAdapter
import com.azamovhudstc.graphqlanilist.ui.screens.wrong_title.SourceSearchDialogFragment
import com.azamovhudstc.graphqlanilist.utils.Result
import com.azamovhudstc.graphqlanilist.utils.dp
import com.azamovhudstc.graphqlanilist.utils.hide
import com.azamovhudstc.graphqlanilist.utils.show
import com.azamovhudstc.graphqlanilist.viewmodel.AnimeWatchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

@AndroidEntryPoint
class AnimeWatchPage() : Fragment() {
    private var end: Int? = null
    private lateinit var epList: MutableList<String>
    private lateinit var epType: String
    private var _binding: FragmentAnimeWatchPageBinding? = null
    private val binding get() = _binding!!
    private var isOnlyOne = false
    lateinit var media:AniListMedia
    lateinit var source:AnimeSource
    private var style = 0
    var screenWidth = 0
    private val model by activityViewModels<AnimeWatchViewModel>()
    private lateinit var headerAdapter: AnimeWatchAdapter
    private lateinit var episodeAdapter: EpisodesAdapter
    private lateinit var list: ArrayList<Data>
    private lateinit var epIndex: String
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
         media = requireArguments().getSerializable("data") as AniListMedia
        headerAdapter = AnimeWatchAdapter(this)
        episodeAdapter = EpisodesAdapter(emptyList(), 0)

        headerAdapter.setItemClickListener {
            if (it == 0) {
                lifecycleScope.launch {

                    val animeSource: YugenSource =
                        SourceSelector(requireContext()).getSelectedSource("yugen") as YugenSource
                    source =animeSource
                    binding.animeSourceRecycler.hide()
                    binding.animeNotSupported.hide()
                    episodeAdapter.list = emptyList()
                    binding.mediaInfoProgressBar.show()
                    model.loadEpisodesImg(media.idMal!!)
                    model.imageList.observe(viewLifecycleOwner) {
                        when (it) {
                            is Result.Error -> {

                            }

                            is Result.Loading -> {
                                headerAdapter.changeWrongTitleVisibility(false)

                            }

                            is Result.Success -> {

                                lifecycleScope.launch {
                                    val list = animeSource.searchAnime(media.title!!.native)
                                    val episodeListForAdapter = ArrayList<Data>()
                                    if (list.isNotEmpty()) {
                                        val animeEpisodesMap =
                                            animeSource.animeDetails(list.get(0).href)
                                        epType = animeEpisodesMap.keys.first()
                                        epList = animeEpisodesMap[epType]!!.keys.toMutableList()
                                        epIndex = epList.first()
                                        var count = 0
                                        for (s in 0 until epList.size) {
                                            var data = epList.get(s)
                                            if (it.data.data.size > s && it.data.data.get(s).images != null && media.idAniList != 1535) {
                                                episodeListForAdapter.add(
                                                    Data(
                                                        " Episode ${data}",
                                                        Images(Jpg(it.data.data.get(s).images!!.jpg.image_url)),
                                                        data.toInt(),
                                                        "${list.get(0).title} $data",
                                                        "?NIULLLLAAABLEEE"
                                                    )
                                                )
                                            } else {
                                                episodeListForAdapter.add(
                                                    Data(
                                                        " Episode ${data}",
                                                        Images(Jpg(media.coverImage.medium)),
                                                        data.toInt(),
                                                        "${list.get(0).title} $data",
                                                        "?NIULLLLAAABLEEE"
                                                    )
                                                )
                                            }
                                        }
                                        binding.animeSourceRecycler.show()
                                        binding.animeNotSupported.hide()
                                        binding.mediaInfoProgressBar.hide()
                                        episodeAdapter.list = episodeListForAdapter
                                        episodeAdapter.notifyDataSetChanged()
                                        headerAdapter.changeWrongTitleVisibility(true)

                                        episodeAdapter.setItemClickListener {
                                            epIndex = epList[it.mal_id - 1]
                                            val data = AnimePlayingDetails(
                                                animeName = media.title!!.userPreferred!!,
                                                animeUrl = list.get(0).href!!,
                                                animeEpisodeIndex = epIndex,
                                                animeEpisodeMap = animeEpisodesMap[epType] as HashMap<String, String>,
                                                animeTotalEpisode = animeEpisodesMap[epType]!!.size.toString(),
                                                epType = epType
                                            )
                                            PlayerActivity.pipStatus = true
                                            PlayerActivity.sourceType = "yugen"

                                            val intent = PlayerActivity.newIntent(
                                                requireContext(),
                                                data
                                            )
                                            startActivity(intent)

                                        }

                                    } else {
                                        binding.animeSourceRecycler.show()
                                        binding.animeNotSupported.show()
                                        binding.mediaInfoProgressBar.hide()
                                        episodeAdapter.list = emptyList()
                                        episodeAdapter.notifyDataSetChanged()
                                    }
                                }
                            }

                            else -> {}
                        }
                    }


                }
            } else {
                lifecycleScope.launch {
                    val animeSource: AnimeSource =
                        SourceSelector(requireContext()).getSelectedSource("aniworld")
                    source =animeSource
                    headerAdapter.changeWrongTitleVisibility(false)

                    binding.animeSourceRecycler.hide()
                    binding.animeNotSupported.hide()
                    episodeAdapter.list = emptyList()
                    binding.mediaInfoProgressBar.show()
                    val list = animeSource.searchAnime(media.title!!.english)
                    val episodeListForAdapter = ArrayList<Data>()
                    if (list.isNotEmpty()) {
                        val animeEpisodesMap = animeSource.animeDetails(list.get(0).href)
                        epType = animeEpisodesMap.keys.first()
                        epList = animeEpisodesMap[epType]!!.keys.toMutableList()
                        val linkList = animeEpisodesMap[epType]!!.values.toMutableList()
                        epIndex = epList.first()
                        epList.onEach {
                            episodeListForAdapter.add(
                                Data(
                                    " Episode ${it}",
                                    Images(Jpg(media.coverImage!!.large!!)),
                                    it.toInt(),
                                    "${list.get(0).title} $it",
                                    "?NIULLLLAAABLEEE"
                                )
                            )
                        }
                        binding.animeSourceRecycler.show()
                        binding.animeNotSupported.hide()
                        binding.mediaInfoProgressBar.hide()
                        episodeAdapter.list = episodeListForAdapter
                        episodeAdapter.notifyDataSetChanged()

                        episodeAdapter.setItemClickListener {
                            epIndex = epList[it.mal_id - 1]
                            val data = AnimePlayingDetails(
                                animeName = media.title!!.userPreferred!!,
                                animeUrl = linkList.get(0),
                                animeEpisodeIndex = epIndex,
                                animeEpisodeMap = animeEpisodesMap[epType] as HashMap<String, String>,
                                animeTotalEpisode = animeEpisodesMap[epType]!!.size.toString(),
                                epType = epType
                            )
                            PlayerActivity.pipStatus = true
                            PlayerActivity.sourceType = "aniworld"

                            val intent = PlayerActivity.newIntent(
                                requireContext(),
                                data
                            )
                            startActivity(intent)

                        }

                    } else {
                        binding.animeSourceRecycler.show()
                        binding.animeNotSupported.show()
                        binding.mediaInfoProgressBar.hide()
                        episodeAdapter.list = emptyList()
                        episodeAdapter.notifyDataSetChanged()
                    }
                }

            }
        }



        screenWidth = resources.displayMetrics.widthPixels.dp.toInt()

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
    }

    fun onIconPressed(style: Int, reversed: Boolean) {
        episodeAdapter.list = episodeAdapter.list.reversed()
        episodeAdapter.notifyDataSetChanged()
    }
@SuppressLint("SuspiciousIndentation")
fun onWrongSelected(){
  val dialog =  SourceSearchDialogFragment(source =source ,media)
      dialog.show(requireActivity().supportFragmentManager, null)
    dialog.setItemSearchListener {sourceDAta ->
        dialog.dismiss()
     lifecycleScope.launch {
         binding.animeSourceRecycler.hide()
         binding.animeNotSupported.hide()
         episodeAdapter.list = emptyList()
         binding.mediaInfoProgressBar.show()
         val episodeListForAdapter = ArrayList<Data>()
             val animeEpisodesMap = source.animeDetails(sourceDAta.href)
             epType = animeEpisodesMap.keys.first()
             epList = animeEpisodesMap[epType]!!.keys.toMutableList()
             val linkList = animeEpisodesMap[epType]!!.values.toMutableList()
             epIndex = epList.first()
             epList.onEach {
                 episodeListForAdapter.add(
                     Data(
                         " Episode ${it}",
                         Images(Jpg(sourceDAta.img!!)),
                         it.toInt(),
                         "${sourceDAta.title} $it",
                         "?NIULLLLAAABLEEE"
                     )
                 )
             }
             binding.animeSourceRecycler.show()
             binding.animeNotSupported.hide()
             binding.mediaInfoProgressBar.hide()
             episodeAdapter.list = episodeListForAdapter
             episodeAdapter.notifyDataSetChanged()

         episodeAdapter.setItemClickListener {
             epIndex = epList[it.mal_id - 1]
             val data = AnimePlayingDetails(
                 animeName = media.title!!.userPreferred!!,
                 animeUrl = sourceDAta.href!!,
                 animeEpisodeIndex = epIndex,
                 animeEpisodeMap = animeEpisodesMap[epType] as HashMap<String, String>,
                 animeTotalEpisode = animeEpisodesMap[epType]!!.size.toString(),
                 epType = epType
             )
             PlayerActivity.pipStatus = true
             PlayerActivity.sourceType = "yugen"

             val intent = PlayerActivity.newIntent(
                 requireContext(),
                 data
             )
             startActivity(intent)

         }

     }

    }
}
    fun onIconPressedForStyle(style: Int) {
        episodeAdapter.updateType(style)
        episodeAdapter.notifyDataSetChanged()

    }
}