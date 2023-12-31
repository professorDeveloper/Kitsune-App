/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.graphqlanilist.bindings.provideImageBinding
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.databinding.ItemRvLayoutBinding
import com.azamovhudstc.graphqlanilist.di.GlideApp
import com.azamovhudstc.graphqlanilist.utils.loadImage
import com.bumptech.glide.load.engine.DiskCacheStrategy

class SearchPagingAdapter : PagingDataAdapter<AniListMedia, SearchPagingAdapter.AnimePageVh>(REPO_COMPARATOR) {
        private lateinit var itemClickListener :((AniListMedia)->Unit)


    fun setOnItemClickListener(listener:((AniListMedia)->Unit)){
        itemClickListener=listener // Sa,ple
    }
    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<AniListMedia>() {
            override fun areItemsTheSame(oldItem: AniListMedia, newItem: AniListMedia): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: AniListMedia, newItem: AniListMedia): Boolean =
                oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: SearchPagingAdapter.AnimePageVh, position: Int) {
        holder.onBind(getItem(position)!!)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchPagingAdapter.AnimePageVh {
        val binding =
            ItemRvLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnimePageVh(binding)
    }

    inner class AnimePageVh(val bin: ItemRvLayoutBinding) :
        RecyclerView.ViewHolder(bin.root) {
        fun onBind(data: AniListMedia) {
            bin.apply {
                val binding = bin
                GlideApp.with(bin.root.context)
                    .load(data.coverImage.large)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(shapeableImageView)
                binding.itemCompactScore.text = (data.averageScore / 10.0).toString()
                binding.title.text =data.title.userPreferred
                binding.genres.text =data.getGenresToString()
                bin.root.setOnClickListener {
                    itemClickListener.invoke(data)
                }
            }
        }
    }

}