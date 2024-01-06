/*
 *  Created by Azamov X ㋡ on 1/5/24, 12:20 AM
 *  Copyright (c) 2024 . All rights reserved.
 *  Last modified 1/5/24, 12:20 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.graphqlanilist.R
import com.azamovhudstc.graphqlanilist.SearchByAnyQuery
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.data.model.ui_models.MediaCoverImage
import com.azamovhudstc.graphqlanilist.data.model.ui_models.MediaTitle
import com.azamovhudstc.graphqlanilist.databinding.CompatAllAnimeBinding
import com.azamovhudstc.graphqlanilist.fragment.HomeMedia
import com.azamovhudstc.graphqlanilist.ui.screens.HomeScreen
import com.azamovhudstc.graphqlanilist.utils.Constants
import com.azamovhudstc.graphqlanilist.utils.animationTransaction
import com.azamovhudstc.graphqlanilist.utils.loadImage
import com.azamovhudstc.graphqlanilist.utils.setAnimation

class AllAnimePageAdapter(
    var list: List<SearchByAnyQuery.Medium?>?,
    private val matchParent: Boolean = false,
    private val activity: HomeScreen
) :
    RecyclerView.Adapter<AllAnimePageAdapter.AllAnimeVh>() {

    inner class AllAnimeVh(private val binding: CompatAllAnimeBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        init {
            if (matchParent) itemView.updateLayoutParams { width = -1 }
        }

        fun onBind(media: HomeMedia) {
            binding.apply {
                itemImg.loadImage(media.coverImage?.medium ?: Constants.IMAGE_URL)
                titleItem.text = media.title?.userPreferred
            }
            binding.root.setOnClickListener { clicked(bindingAdapterPosition) }
        }
    }

    private fun clicked(bindingAdapterPosition: Int) {
        val bundle = Bundle()
        val anilistMedia = AniListMedia(

            list?.get(bindingAdapterPosition)!!.homeMedia.id,
            list!!.get(bindingAdapterPosition)!!.homeMedia.idMal ?: 0,
            MediaTitle(
                list!!.get(bindingAdapterPosition)!!.homeMedia.title?.romaji ?: "",
                list!!.get(bindingAdapterPosition)!!.homeMedia.title?.userPreferred ?: "",
                list!!.get(bindingAdapterPosition)!!.homeMedia.title?.romaji.toString()
            ),
            coverImage = MediaCoverImage(
                list?.get(bindingAdapterPosition)!!.homeMedia.coverImage?.extraLarge!!,
                list?.get(bindingAdapterPosition)!!.homeMedia.coverImage?.large!!,
                list?.get(bindingAdapterPosition)!!.homeMedia?.coverImage?.medium!!
            )
        )
        bundle.putSerializable("data", anilistMedia)
        activity.findNavController()
            .navigate(R.id.detailScreen, bundle, animationTransaction().build())

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllAnimeVh {

        val binding =
            CompatAllAnimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AllAnimeVh(
            binding
        )
    }


    override fun onBindViewHolder(holder: AllAnimeVh, position: Int) {

        holder.onBind(list!!.get(position)!!.homeMedia)
    }


    override fun getItemCount(): Int {
        return list!!.size
    }

    fun submitList(newList: ArrayList<SearchByAnyQuery.Medium>) {
        list = newList
    }
}