package com.azamovhudstc.graphqlanilist.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.graphqlanilist.bindings.provideImageBinding
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.databinding.ItemRvBinding
import com.azamovhudstc.graphqlanilist.di.GlideApp
import com.azamovhudstc.graphqlanilist.utils.loadImage
import com.bumptech.glide.load.engine.DiskCacheStrategy

class SearchPagingAdapter :
    PagingDataAdapter<AniListMedia, SearchPagingAdapter.AnimePageVh>(REPO_COMPARATOR) {
        private lateinit var itemClickListener :((AniListMedia)->Unit)


    fun setOnItemClickListener(listener:((AniListMedia)->Unit)){
        itemClickListener=listener
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
            ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnimePageVh(binding)
    }

    inner class AnimePageVh(val bin: ItemRvBinding) :
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