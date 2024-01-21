/*
 *  Created by Azamov X ㋡ on 1/20/24, 4:19 PM
 *  Copyright (c) 2024 . All rights reserved.
 *  Last modified 1/20/24, 4:19 PM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.graphqlanilist.MediaCompatBindingModel_
import com.azamovhudstc.graphqlanilist.data.model.SourceModel
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AnimePlayingDetails
import com.azamovhudstc.graphqlanilist.databinding.BottomSheetSourceSearchBinding
import com.azamovhudstc.graphqlanilist.databinding.ItemVerticalLayoutBinding
import com.azamovhudstc.graphqlanilist.databinding.SourceItemBinding
import com.azamovhudstc.graphqlanilist.utils.loadImage

class SourceAdapter():RecyclerView.Adapter<SourceAdapter.SourceVh>() {
    var list: ArrayList<SourceModel> = ArrayList()
    lateinit var itemSelectListenerd:(SourceModel)->Unit
    fun setItemSelectListener(listener:(SourceModel)->Unit){
        itemSelectListenerd =listener
    }

    fun submitList(newList:ArrayList<SourceModel>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
    inner class SourceVh(var binding: SourceItemBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(animeDetails:SourceModel){
            binding.apply {
                itemImg.loadImage(animeDetails.img)
                titleItem.text=animeDetails.title
                binding.root.setOnClickListener {
                    itemSelectListenerd.invoke(animeDetails)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceVh {
        return SourceVh(SourceItemBinding.inflate(LayoutInflater.from(parent.context,),parent,false))
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: SourceVh, position: Int) {
        holder.onBind(list.get(position))
    }
}