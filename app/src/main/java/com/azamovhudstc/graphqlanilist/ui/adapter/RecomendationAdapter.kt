package com.azamovhudstc.graphqlanilist.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.graphqlanilist.DetailFullDataQuery
import com.azamovhudstc.graphqlanilist.databinding.ItemMediaCompatBinding
import com.azamovhudstc.graphqlanilist.utils.loadImage
import com.azamovhudstc.graphqlanilist.utils.setAnimation

class RecommendationAdapter (private val list :List<DetailFullDataQuery.Node4?>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var listener:((DetailFullDataQuery.Node4)->Unit)
    fun setItemClickListener(itemListener:((DetailFullDataQuery.Node4)->Unit)){
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
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val b = (holder as MediaViewHolder).binding
        setAnimation(b.root.context, holder.binding.root)
        val media = list?.getOrNull(position)
        if (media != null) {
            b.root.setOnClickListener {
                listener.invoke(list.get(position)!!)
            }

            b.itemCompactImage.loadImage(media.mediaRecommendation?.coverImage?.large)
            b.itemCompactTitle.text = media.mediaRecommendation!!.title!!.userPreferred?:"UNKNOWN"
            b.itemCompactScore.text =
                ((if (media.mediaRecommendation.meanScore != 0) (media.mediaRecommendation.meanScore
                    ?: 0) else 0) / 10.0).toString()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}