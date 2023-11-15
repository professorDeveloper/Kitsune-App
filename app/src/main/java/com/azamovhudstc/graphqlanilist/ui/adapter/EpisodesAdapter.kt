package com.azamovhudstc.graphqlanilist.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.graphqlanilist.data.network.rest.jikan.Data
import com.azamovhudstc.graphqlanilist.databinding.ItemEpisodeListBinding
import com.azamovhudstc.graphqlanilist.utils.Constants.Companion.IMAGE_URL
import com.azamovhudstc.graphqlanilist.utils.setAnimation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl

class EpisodesAdapter( var list: List<Data>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var listener: ((Data) -> Unit)
    fun setItemClickListener(itemListener: ((Data) -> Unit)) {
        listener = itemListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MediaViewHolder(
            ItemEpisodeListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class MediaViewHolder(val binding: ItemEpisodeListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val b = (holder as MediaViewHolder).binding
        setAnimation(b.root.context, holder.binding.root)
        b.root.setOnClickListener {
            listener.invoke(list.get(position))
        }
        val media = list?.getOrNull(position)
        if (media != null) {
            val thumb =
                media.images.jpg.image_url?.let { if (it.isNotEmpty()) GlideUrl(it)  else null }
            Glide.with(b.itemEpisodeImage).load(thumb ?: IMAGE_URL).override(400, 0)
                .into(b.itemEpisodeImage)
            b.apply {
                itemEpisodeTitle.text=media.title
                itemEpisodeNumber.text=media.mal_id.toString()

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}