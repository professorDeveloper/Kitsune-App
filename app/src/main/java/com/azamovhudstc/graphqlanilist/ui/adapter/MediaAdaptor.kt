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
import com.azamovhudstc.graphqlanilist.DetailFullDataQuery
import com.azamovhudstc.graphqlanilist.databinding.ItemMediaCompatBinding
import com.azamovhudstc.graphqlanilist.utils.loadImage
import com.azamovhudstc.graphqlanilist.utils.setAnimation

class MediaAdaptor(private val list: List<DetailFullDataQuery.Edge1?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var listener:((DetailFullDataQuery.Edge1)->Unit)
    fun setItemClickListener(itemListener:((DetailFullDataQuery.Edge1)->Unit)){
        listener=itemListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MediaViewHolder(
            ItemMediaCompatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class MediaViewHolder(val binding: ItemMediaCompatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.invoke(list.get(absoluteAdapterPosition)!!)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val b = (holder as MediaViewHolder).binding
        setAnimation(b.root.context, holder.binding.root)
        val media = list?.getOrNull(position)
        if (media != null) {
            b.itemCompactImage.loadImage(media.node?.coverImage?.large)
            b.itemCompactTitle.text = media.node!!.title!!.userPreferred
            b.itemCompactScore.text =
                ((if (media.node.meanScore != 0) (media.node.meanScore
                    ?: 0) else 0) / 10.0).toString()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}