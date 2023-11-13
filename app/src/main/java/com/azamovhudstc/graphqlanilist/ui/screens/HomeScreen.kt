package com.azamovhudstc.graphqlanilist.ui.screens

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.azamovhudstc.graphqlanilist.R
import com.azamovhudstc.graphqlanilist.databinding.HomeScreenBinding
import com.azamovhudstc.graphqlanilist.type.SortType
import com.azamovhudstc.graphqlanilist.ui.screens.controller.PagingSearchController
import com.azamovhudstc.graphqlanilist.utils.collectLatest
import com.azamovhudstc.graphqlanilist.utils.dismissKeyboard
import com.azamovhudstc.graphqlanilist.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeScreen : Fragment(R.layout.home_screen) {
    private var _binding: HomeScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SearchViewModel>()
    private lateinit var pagingController: PagingSearchController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = HomeScreenBinding.bind(view)
        pagingController = PagingSearchController()
        val list = ArrayList<SortType>()
        list.add(SortType.TRENDING)

        binding.progress.isVisible =false
        binding.searchRecycler.isVisible=true
        viewModel.onSearchQueryChanged(query = "Trending", type = list.toMutableList())

        observeViewModel()
        binding.searchRecycler.setController(pagingController)
        binding.mainSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                dismissKeyboard(binding.mainSearch)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                println("tushdi")
                if (query.toString().isNotEmpty()){
                    binding.progress.isVisible = true
                    binding.searchRecycler.isVisible = false
                    viewModel.onSearchQueryChanged(query)
                }else{
                    binding.progress.isVisible =false
                    binding.searchRecycler.isVisible=true
                }
                return false
            }
        })


    }


    override fun onPause() {
        super.onPause()
    }

    private fun observeViewModel() {

        collectLatest(viewModel.searchList) { animeData ->

            binding.progress.isVisible = false
            binding.searchRecycler.isVisible = true
            pagingController . submitData (animeData)

        }


    }


}