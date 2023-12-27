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
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.graphqlanilist.data.network.rest.jikan.Data
import com.azamovhudstc.graphqlanilist.databinding.ItemEpisodeGridBinding
import com.azamovhudstc.graphqlanilist.databinding.ItemEpisodeListBinding
import com.azamovhudstc.graphqlanilist.utils.loadImage
import com.azamovhudstc.graphqlanilist.utils.setAnimation

class EpisodesAdapter(var list: List<Data>, var type: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var listener: ((Data) -> Unit)
    fun setItemClickListener(itemListener: ((Data) -> Unit)) {
        listener = itemListener
    }

    override fun getItemViewType(position: Int): Int {
        return type
    }

    inner class EpisodeGridViewHolder(val binding: ItemEpisodeGridBinding) :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return (when (viewType) {
            0 -> EpisodeListViewHolder(
                ItemEpisodeListBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
            1 -> EpisodeGridViewHolder(
                ItemEpisodeGridBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
            else -> throw IllegalArgumentException()

        }
                )
    }

    inner class EpisodeListViewHolder(val binding: ItemEpisodeListBinding) :RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EpisodeListViewHolder -> {
                val media = list?.getOrNull(position)
                val binding = holder.binding
                binding.root.setOnClickListener {
                    listener.invoke(list[position])
                }
                setAnimation(binding.root.context, holder.binding.root)
                val thumb = media?.images!!.jpg.image_url
                binding.itemEpisodeImage.loadImage(thumb)
                binding.itemEpisodeNumber.text = media.mal_id.toString()
                binding.itemEpisodeTitle.text = media.title

            }

            is EpisodeGridViewHolder -> {
                val binding = holder.binding
                binding.root.setOnClickListener {
                    listener.invoke(list[position])
                }

                setAnimation(binding.root.context, holder.binding.root)
                val media = list?.getOrNull(position)
                val thumb = media?.images!!.jpg.image_url
                binding.itemEpisodeImage.loadImage(thumb)
                binding.itemEpisodeNumber.text = media.mal_id.toString()
                binding.itemEpisodeTitle.text = media.title
            }

        }

    }

    fun updateType(t: Int) {
        type = t
    }

    override fun getItemCount(): Int {
        return list.size
    }
}