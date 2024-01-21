/*
 *  Created by Azamov X ㋡ on 1/20/24, 4:07 PM
 *  Copyright (c) 2024 . All rights reserved.
 *  Last modified 1/20/24, 4:07 PM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.ui.screens.wrong_title

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.math.MathUtils.clamp
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.azamovhudstc.graphqlanilist.data.model.SourceModel
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.data.model.ui_models.Media
import com.azamovhudstc.graphqlanilist.databinding.BottomSheetSourceSearchBinding
import com.azamovhudstc.graphqlanilist.source.AnimeSource
import com.azamovhudstc.graphqlanilist.ui.adapter.AnimeWatchAdapter
import com.azamovhudstc.graphqlanilist.ui.adapter.MediaAdaptor
import com.azamovhudstc.graphqlanilist.ui.adapter.SourceAdapter
import com.azamovhudstc.graphqlanilist.utils.hide
import com.azamovhudstc.graphqlanilist.utils.px
import com.azamovhudstc.graphqlanilist.utils.show
import com.azamovhudstc.graphqlanilist.utils.tryWithSuspend
import com.azamovhudstc.graphqlanilist.viewmodel.SourceViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SourceSearchDialogFragment(private val source: AnimeSource, private val media: AniListMedia) :
    BottomSheetDialogFragment() {

    private var _binding: BottomSheetSourceSearchBinding? = null
    private val model by viewModels<SourceViewModel>()
    private val binding get() = _binding!!
    private var searched = false
    var anime = true
    var i: Int? = null
    var id: Int? = null
    private lateinit var setItemSearchListener: ((SourceModel) -> Unit)

    fun setItemSearchListener(listener: (SourceModel) -> Unit) {
        setItemSearchListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSourceSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val scope = requireActivity().lifecycleScope
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        fun search() {
            binding.searchBarText.clearFocus()
            imm.hideSoftInputFromWindow(binding.searchBarText.windowToken, 0)
            scope.launch {
                model.search(binding.searchBarText.text.toString(),source).onEach {
                    binding.searchRecyclerView.visibility = View.VISIBLE
                    binding.mediaListProgressBar.hide()
                    binding.mediaListLayout.show()
                    binding.searchProgress.visibility = View.GONE
                    val adapter = SourceAdapter()
                    binding.searchRecyclerView.adapter = adapter
                    adapter.submitList(it)
                    adapter.setItemSelectListener {
                        setItemSearchListener.invoke(it)
                    }
                }.launchIn(lifecycleScope)
            }
        }

        binding.searchBarText.setText(media!!.title!!.english)
        binding.searchBarText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    search()
                    true
                }

                else -> false
            }
        }
        binding.searchBar.setEndIconOnClickListener { search() }
        if (!searched) search()
        searched = true
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}