/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.graphqlanilist.R
import com.azamovhudstc.graphqlanilist.databinding.AnimeWatchItemBinding
import com.azamovhudstc.graphqlanilist.ui.screens.detail.pages.AnimeWatchPage
import com.azamovhudstc.graphqlanilist.ui.screens.wrong_title.SourceSearchDialogFragment

class AnimeWatchAdapter(private val fragment: AnimeWatchPage) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isDoubleClick = false
    private var reversed = false
    private var oldPosition = -1
    private var style = 0
    private var _binding: AnimeWatchItemBinding? = null
    private lateinit var listener: ((Int) -> Unit)
    fun setItemClickListener(itemListener: ((Int) -> Unit)) {
        listener = itemListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MediaViewHolder(
            AnimeWatchItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class MediaViewHolder(val binding: AnimeWatchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MediaViewHolder).binding
        _binding = binding
        binding.animeSourceTop.setOnClickListener {
            reversed = true
            binding.animeSourceTop.rotation = if (reversed) -90f else 90f
            fragment.onIconPressed(style, reversed)
        }
        binding.animeSource.setAdapter(
            ArrayAdapter(
                fragment.requireContext(),
                R.layout.item_dropdown,
                listOf("YUGEN","ANIWORLD (GERMAN)")
            )
        )
        binding.animeSource.setOnItemClickListener { _, view, i, _ ->
            if (!isDoubleClick || i != oldPosition) {
                listener.invoke(i)
                isDoubleClick = true
                oldPosition = i
            }
        }
        var selected = when (style) {
            0 -> binding.animeSourceList
            1 -> binding.animeSourceGrid
            else -> binding.animeSourceList
        }
        selected.alpha = 1f
        fun selected(it: ImageView) {
            selected.alpha = 0.33f
            selected = it
            selected.alpha = 1f
        }

        binding.animeSourceSearch.setOnClickListener {
            fragment.onWrongSelected()
        }



        binding.animeSourceList.setOnClickListener {
            selected(it as ImageView)
            style = 0
            fragment.onIconPressedForStyle(style)
        }
        binding.animeSourceGrid.setOnClickListener {
            selected(it as ImageView)
            style = 1
            fragment.onIconPressedForStyle(style)
        }


    }

    fun changeWrongTitleVisibility (visibility:Boolean){
        _binding?.animeSourceSearch?.isVisible=visibility
    }
    //Chips
    @SuppressLint("SetTextI18n")
    fun updateChips(totalPage: Int, defaultSize: Int, lastListSize: Int) {
//        val binding = _binding
//        if (binding != null) {
//
//            val screenWidth = fragment.screenWidth
//            var select: Chip? = null
//            var arr =ArrayList<String>()
//            for ()
//
//            for (position in arr.indices) {
//                val chip =
//                    ItemChipBinding.inflate(
//                        LayoutInflater.from(fragment.context),
//                        binding.animeSourceChipGroup,
//                        false
//                    ).root
//                chip.isCheckable = true
//                fun selected() {
//                    chip.isChecked = true
//                    binding.animeWatchChipScroll.smoothScrollTo(
//                        (chip.left - screenWidth / 2) + (chip.width / 2),
//                        0
//                    )
//                }
//
//                chip.setOnClickListener {
//                    selected()
//                    fragment.onChipClicked(position, limit * (position), last - 1)
//                }
//                binding.animeSourceChipGroup.addView(chip)
//                if (selected == position) {
//                    selected()
//                    select = chip
//                }
//            }
//            if (select != null)
//                binding.animeWatchChipScroll.apply {
//                    post {
//                        scrollTo(
//                            (select.left - screenWidth / 2) + (select.width / 2),
//                            0
//                        )
//                    }
//                }
//    }
    }

    override fun getItemCount(): Int {
        return 1
    }
}