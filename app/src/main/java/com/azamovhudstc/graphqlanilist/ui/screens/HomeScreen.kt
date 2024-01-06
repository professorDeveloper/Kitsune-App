/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.graphqlanilist.data.model.SearchResults
import com.azamovhudstc.graphqlanilist.databinding.HomeScreenBinding
import com.azamovhudstc.graphqlanilist.ui.adapter.AllAnimePageAdapter
import com.azamovhudstc.graphqlanilist.ui.adapter.ProgressAdapter
import com.azamovhudstc.graphqlanilist.utils.Resource
import com.azamovhudstc.graphqlanilist.utils.hide
import com.azamovhudstc.graphqlanilist.utils.show
import com.azamovhudstc.graphqlanilist.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class HomeScreen : Fragment() {
    private var _binding: HomeScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SearchViewModel>()
    lateinit var result: SearchResults
    private lateinit var progressAdapter: ProgressAdapter
    private var screenWidth: Float = 0f
    private lateinit var job: Job
    private lateinit var allAnimePageAdapter: AllAnimePageAdapter
    private lateinit var allAnimePageAdapterForSearch: AllAnimePageAdapter
    private lateinit var concatAdapter: ConcatAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomeScreenBinding.inflate(
            inflater,
            container,
            false
        )
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        screenWidth = resources.displayMetrics.run { widthPixels / density }
        initUIWithLocalData()
        result = viewModel.searchResults
        initProgress()
        allAnimePageAdapter =
            AllAnimePageAdapter(viewModel.searchResults.results, true, this)
        concatAdapter = ConcatAdapter(allAnimePageAdapter, progressAdapter)
        binding.searchRecycler.adapter = concatAdapter
        super.onViewCreated(view, savedInstanceState)

        binding.searchRecycler.layoutManager =
            GridLayoutManager(requireContext(), (screenWidth / 124f).toInt())

        initPagination()
        observerLoadData()
    }

    private fun initUIWithLocalData() {
        if (viewModel.notSet) {
            viewModel.notSet = false
            viewModel.searchResults = SearchResults(
                "ANIME",
                isAdult = false,
                onList = false,
                results = arrayListOf(),
                hasNextPage = true,
                sort = "TRENDING_DESC"
            )
        }
    }


    private fun observerLoadData() {
        viewModel.searchResult.observe(viewLifecycleOwner) {
            when (it) {
                Resource.Loading -> {
                    binding.progress.show()
                    binding.searchRecycler.hide()
                }
                is Resource.Error -> {

                }
                is Resource.Success -> {
                    binding.progress.hide()
                    binding.searchRecycler.hide()
                    binding.mainRv.show()
                    val it = it.data
                    if (it != null) {
                        allAnimePageAdapterForSearch = AllAnimePageAdapter(
                            it.results, true, activity = this
                        )
                        progressAdapter.bar!!.hide()
                        binding.mainRv.adapter = allAnimePageAdapterForSearch
                        binding.mainRv.layoutManager =
                            GridLayoutManager(requireContext(), (screenWidth / 124f).toInt())
                    }

                }
            }
        }
        viewModel.result.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                }
                Resource.Loading -> {
                }
                is Resource.Success -> {
                    val it = it.data
                    if (it != null) {
                        binding.mainRv.hide()
                        viewModel.searchResults.apply {
                            onList = it.onList
                            isAdult = it.isAdult
                            perPage = it.perPage
                            search = it.search
                            sort = it.sort
                            genres = it.genres
                            excludedGenres = it.excludedGenres
                            excludedTags = it.excludedTags
                            tags = it.tags
                            season = it.season
                            seasonYear = it.seasonYear
                            format = it.format
                            page = it.page
                            hasNextPage = it.hasNextPage
                        }

                        val prev = viewModel.searchResults.results!!.size
                        viewModel.searchResults.results!!.addAll(it.results!!)
                        allAnimePageAdapter.notifyItemRangeInserted(
                            prev,
                            it.results!!.size
                        )
                        progressAdapter.bar?.visibility =
                            if (it.hasNextPage) View.VISIBLE else View.GONE

                        binding.apply {
                            this.mainSearch.setOnCloseListener {
                                viewModel.loadAllAnimeList()
                                true
                            }
                            this.mainSearch.setOnQueryTextListener(object :
                                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                                override fun onQueryTextSubmit(query: String?): Boolean {
                                    if (query.toString().trim().isNotEmpty()) {
                                        job = lifecycleScope.launchWhenCreated {
                                            viewModel.onSearchQueryChanged(query.toString())
                                        }

                                        job.start()
                                    } else {
                                        if (::job.isInitialized) {
                                            job.cancel()
                                            binding.mainRv.hide()
                                            binding.searchRecycler.show()
                                            binding.mainRv.hide()
                                            binding.searchRecycler.show()
                                            binding.searchRecycler.scrollToPosition(0)
                                        }
                                    }

                                    return true
                                }

                                override fun onQueryTextChange(newText: String?): Boolean {
                                    if (newText.toString().trim().isNotEmpty()) {
                                        job = lifecycleScope.launchWhenCreated {
                                            viewModel.onSearchQueryChanged(newText.toString())
                                        }
                                        job.start()

                                    } else {
                                        if (::job.isInitialized) {
                                            job.cancel()
                                            binding.mainRv.hide()
                                            binding.searchRecycler.show()
                                            binding.searchRecycler.scrollToPosition(0)
                                        }
                                    }
                                    return true

                                }
                            }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun initPagination() {
        binding.searchRecycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(v: RecyclerView, dx: Int, dy: Int) {
                if (!v.canScrollVertically(1)) {
                    if (viewModel.searchResults.hasNextPage && viewModel.searchResults.results!!.isNotEmpty() && !loading) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.loadNextPage(viewModel.searchResults)
                        }
                    }
                }
                super.onScrolled(v, dx, dy)
            }
        })
    }

    private fun initProgress() {
        val notSet = viewModel.notSet
        progressAdapter = ProgressAdapter(searched = viewModel.searched)

        progressAdapter.ready.observe(viewLifecycleOwner) {
            if (it == true) {
                if (!notSet) {
                    if (!viewModel.searched) {
                        viewModel.searched = true
                    }
                }
                loadData()
            }
        }

    }


    private var searchTimer = Timer()
    private var loading = false

    fun loadData() {
        val size = viewModel.searchResults.results!!.size
        viewModel.searchResults.results!!.clear()
        requireActivity().runOnUiThread {
            allAnimePageAdapter.notifyItemRangeRemoved(0, size)
        }
        progressAdapter.bar?.visibility = View.VISIBLE

        searchTimer.cancel()
        searchTimer.purge()
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                lifecycleScope.launch(Dispatchers.IO) {
                    loading = true
                    viewModel.loadAllAnimeList()
                    loading = false
                }
            }
        }
        searchTimer = Timer()
        searchTimer.schedule(timerTask, 500)
    }


}