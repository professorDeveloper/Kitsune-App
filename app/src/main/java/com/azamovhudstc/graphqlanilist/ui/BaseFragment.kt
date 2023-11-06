package com.azamovhudstc.graphqlanilist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint

abstract class BaseFragment<VM : ViewModel, VB : ViewBinding> : Fragment() {

    protected abstract val viewModel: VM
    protected lateinit var binding: VB

    abstract fun observeViewModel()
    protected abstract fun initViews()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        binding = getViewBinding()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initViews()
    }

    /**
     * It returns [VB] which is assigned to [mViewBinding] and used in [onCreate]
     */
    abstract fun getViewBinding(): VB
}