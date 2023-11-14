package com.azamovhudstc.graphqlanilist.ui.screens.character.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.graphqlanilist.CharacterDataByIDQuery
import com.azamovhudstc.graphqlanilist.DetailFullDataQuery
import com.azamovhudstc.graphqlanilist.databinding.ItemMediaCompatBinding
import com.azamovhudstc.graphqlanilist.utils.loadImage
import com.azamovhudstc.graphqlanilist.utils.setAnimation

class CharacterItemAdapter ( private val list: List<CharacterDataByIDQuery.Edge?>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var listener:((CharacterDataByIDQuery.Edge)->Unit)
    fun setItemClickListener(itemListener:((CharacterDataByIDQuery.Edge)->Unit)){
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
        itemView.updateLayoutParams { width = -1 }
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