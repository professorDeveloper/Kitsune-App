package com.azamovhudstc.graphqlanilist.ui.screens

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.azamovhudstc.graphqlanilist.R
import com.azamovhudstc.graphqlanilist.databinding.HomeScreenBinding
import com.azamovhudstc.graphqlanilist.ui.adapter.SearchPagingAdapter
import com.azamovhudstc.graphqlanilist.utils.collectLatest
import com.azamovhudstc.graphqlanilist.utils.dismissKeyboard
import com.azamovhudstc.graphqlanilist.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeScreen : Fragment(R.layout.home_screen) {
    private var _binding: HomeScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SearchViewModel>()
    private val adapter by lazy { SearchPagingAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = HomeScreenBinding.bind(view)

        observeViewModel()


        binding.apply {
            mainSearch.setOnCloseListener {
                binding.searchRecycler.visibility=View.GONE
                binding.placeHolde.visibility=View.VISIBLE

            true}
            mainSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    dismissKeyboard(binding.mainSearch)
                    return false
                }

                override fun onQueryTextChange(query: String): Boolean {
                    viewModel.onSearchQueryChanged(query)
                    return false
                }
            })
        }

    }


    private fun observeViewModel() {

        collectLatest(viewModel.searchList) { animeData ->
            println(animeData.apply {
                this.toString()
            })
            binding.searchRecycler.visibility=View.VISIBLE
            binding.placeHolde.visibility=View.GONE
            adapter.submitData(animeData)
            binding.searchRecycler.adapter = adapter
        }
    }

}