@file:OptIn(FlowPreview::class)

package com.azamovhudstc.graphqlanilist.ui.screens

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.azamovhudstc.graphqlanilist.R
import com.azamovhudstc.graphqlanilist.bindings.getColors
import com.azamovhudstc.graphqlanilist.bindings.setHtmlText
import com.azamovhudstc.graphqlanilist.data.mapper.MediaStatusAnimity
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.data.model.ui_models.Genre
import com.azamovhudstc.graphqlanilist.databinding.DetailScreenBinding
import com.azamovhudstc.graphqlanilist.utils.Constants.Companion.showSnack
import com.azamovhudstc.graphqlanilist.utils.parseTime
import com.azamovhudstc.graphqlanilist.viewmodel.DetailsViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DetailScreen : Fragment(R.layout.detail_screen) {

    private val viewModel: DetailsViewModel by viewModels()
    private val animeDetails get() = requireArguments().getSerializable("data") as AniListMedia
    private var binding: DetailScreenBinding? = null

    private lateinit var title: String
    private var check by Delegates.notNull<Boolean>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DetailScreenBinding.bind(view)
        check = animeDetails.isFavourite
        observeViewModel()
        initViews()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun observeViewModel() {
        fetchAnimeInfo()
        showLatestEpisodeReleaseTime()
    }

    private fun initViews() {
        animeDetails.let { animeInfo ->
            viewModel.animeMetaModel.update {
                it.copy(
                    idAniList = animeInfo.idAniList,
                    idMal = animeInfo.idMal
                )
            }
            binding?.apply {
                detailsPoster.load(animeInfo.coverImage.large) { crossfade(true) }
                resultTitle.text = animeInfo.title.userPreferred
                title = animeInfo.title.userPreferred


                setType.setOnClickListener {
//                    showMenu(it, R.menu.popup_menu)
                }
            }
        }
    }

    /**
     * It fetches the anime info and displays it on the screen.
     */
    private fun fetchAnimeInfo() {
        animeDetails.let { info ->
            binding?.apply {
                animeInfoLayout.synopsisExpand
                    .setHtmlText(info.description.removeSource())
                releaseDate.text = info.startDate?.getDate()
                status.text = info.status?.name
                type.text = info.type?.rawValue
                releaseTime.text = info.nextAiringEpisode.takeIf {
                    it != null
                }?.run(::displayInDayDateTimeFormat)

                animeInfoLayout.expandableText.visibility = VISIBLE
                releaseDate.visibility = VISIBLE
                status.visibility = VISIBLE
                type.visibility = VISIBLE

                createGenreChips(info.genres)
                setType.text = info.mediaListEntry?.let {
                    when (it) {
                        MediaStatusAnimity.COMPLETED -> "Completed"
                        MediaStatusAnimity.WATCHING -> "Watching"
                        MediaStatusAnimity.DROPPED -> "Dropped"
                        MediaStatusAnimity.PAUSED -> "Paused"
                        MediaStatusAnimity.PLANNING -> "Planning"
                        MediaStatusAnimity.REPEATING -> "Repeating"
                        MediaStatusAnimity.NOTHING -> "Add to list"
                    }
                }
            }
        }
    }

    /**
     * It takes a number of seconds since the epoch and returns a string in the format "Day, dd Month
     * yyyy, hh:mm a"
     *
     * @param seconds The number of seconds since January 1, 1970 00:00:00 UTC.
     * @return The date in the format of Day, Date Month Year, Hour:Minute AM/PM
     */
    private fun displayInDayDateTimeFormat(seconds: Int): String {
        val dateFormat = SimpleDateFormat("E, dd MMM yyyy, hh:mm a", Locale.getDefault())
        val date = Date(seconds * 1000L)
        return dateFormat.format(date)
    }

    /**
     * It creates a chip for each genre in the list.
     *
     * @param genre List<Genre> - The list of genres that we want to display.
     */
    private fun createGenreChips(genres: List<Genre>) {
        binding?.genreGroup?.apply {
            removeAllViews()
            genres.forEach { genre ->
                val (bgColor, outlineColor) = genre.getColors()
                addView(
                    Chip(requireContext()).apply {
                        text = genre.name
                        setTextColor(Color.WHITE)
                        chipStrokeWidth = 3f
                        chipBackgroundColor = bgColor
                        chipStrokeColor = outlineColor
                    }
                )
            }
        }
    }



    private fun toggleFavoriteStatus() {
        check = !check
        val message = if (check) {
            "Anime added to Favorites"
        } else {
            "Anime removed from Favorites"
        }
        showSnack(binding?.root, message)
    }


    /**
     * It shows a popup menu when the user clicks on the view.
     *
     * @param v View - The view that the popup menu should be anchored to.
     * @param menuRes The menu resource to inflate.
     */

    /**
     * It fetches the episode list from the view model and then populates the recycler view with the
     * episode list
     */
    @ExperimentalCoroutinesApi


    private fun startAppBarCloseTimer() {
        lifecycleScope.launch {
            while (true) {
                delay(500)
                binding
                    ?.appbar
                    ?.setExpanded(true, true)
            }
        }
    }

    private fun goToDesiredPosition() {
//        if (desiredPosition != 0) {
//            binding?.appbar?.setExpanded(false, true)
//            binding?.chunkedEpisodeTab?.getTabAt(desiredPosition / 50)?.select()
//        }
    }

    private fun showLatestEpisodeReleaseTime() {
        binding?.releaseTime?.text = animeDetails.nextAiringEpisode?.parseTime {
            binding?.nextEpisodeContainer?.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // letting go of the resources to avoid memory leak.
        binding = null
    }
}

private fun String.removeSource(): String {
    val regex = Regex("\\(Source:.*\\)")
    var text = this
    text = regex.replace(text, "").trim()
    text = text.replace("\n$", "")
    return text
}