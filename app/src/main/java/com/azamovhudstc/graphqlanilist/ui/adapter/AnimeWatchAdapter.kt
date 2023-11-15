package com.azamovhudstc.graphqlanilist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.graphqlanilist.R
import com.azamovhudstc.graphqlanilist.data.network.rest.jikan.Data
import com.azamovhudstc.graphqlanilist.databinding.AnimeWatchItemBinding
import com.azamovhudstc.graphqlanilist.databinding.ItemEpisodeListBinding
import com.azamovhudstc.graphqlanilist.ui.screens.detail.pages.AnimeWatchPage
import com.azamovhudstc.graphqlanilist.utils.Constants
import com.azamovhudstc.graphqlanilist.utils.setAnimation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AnimeWatchAdapter(private val fragment:AnimeWatchPage) :  RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isDoubleClick =false
    private var reversed =false
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
        binding.animeSourceTop.setOnClickListener {
            reversed = !reversed
            binding.animeSourceTop.rotation = if (reversed) -90f else 90f
            fragment.onIconPressed( reversed)
        }
        binding.animeSource.setAdapter(ArrayAdapter(fragment.requireContext(), R.layout.item_dropdown, listOf("JIKAN")))
        binding.animeSource.setOnItemClickListener { _, _, i, _ ->
            if (!isDoubleClick){
                listener.invoke(i)
                isDoubleClick=true
            }
        }

    }

    override fun getItemCount(): Int {
        return 1
    }
}