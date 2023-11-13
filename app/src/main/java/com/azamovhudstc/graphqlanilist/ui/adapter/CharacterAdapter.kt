package com.azamovhudstc.graphqlanilist.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.graphqlanilist.DetailFullDataQuery
import com.azamovhudstc.graphqlanilist.databinding.ItemCharacterBinding
import com.azamovhudstc.graphqlanilist.ui.activity.CharacterDetailsActivity
import com.azamovhudstc.graphqlanilist.utils.Constants.Companion.IMAGE_URL
import com.azamovhudstc.graphqlanilist.utils.loadImage
import com.azamovhudstc.graphqlanilist.utils.setAnimation
import com.draggable.library.extension.ImageViewerHelper
import java.io.Serializable


class CharacterAdapter(
    private val characterList: List<DetailFullDataQuery.Edge?>,
    private val imageList:List<String>
) : RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder>() {
    fun setItemClickListener(listener:((ImageView, String, Int,List<String>)->Unit)){
        listenerClick=listener
    }
    private lateinit var listenerClick:((ImageView,String,Int,List<String>)->Unit)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val binding =
            ItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CharacterViewHolder(binding)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val binding = holder.binding

        val character = characterList[position]
        setAnimation(binding.root.context, holder.binding.root)
        binding.itemCompactRelation.text = character!!.role?.name ?: ("??" + "  ")
        binding.itemCompactImage.loadImage(character.node?.image?.medium ?: IMAGE_URL)
        binding.itemCompactTitle.text = character.node?.name?.userPreferred
        holder.itemView.setOnClickListener {
            listenerClick.invoke(binding.itemCompactImage,
                (character.node?.image?.medium?: IMAGE_URL),position,imageList)

        }
    }

    override fun getItemCount(): Int = characterList.size
    inner class CharacterViewHolder(val binding: ItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {

            }
        }
    }
}