package com.azamovhudstc.graphqlanilist.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.graphqlanilist.DetailFullDataQuery
import com.azamovhudstc.graphqlanilist.databinding.ItemCharacterBinding
import com.azamovhudstc.graphqlanilist.utils.Constants.Companion.IMAGE_URL
import com.azamovhudstc.graphqlanilist.utils.loadImage
import com.azamovhudstc.graphqlanilist.utils.setAnimation


class CharacterAdapter(
    private val characterList: List<DetailFullDataQuery.Edge?>,
) : RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder>() {
    fun setItemClickListener(listener:((DetailFullDataQuery.Edge, String, Int)->Unit)){
        listenerClick=listener
    }
    private lateinit var listenerClick:((DetailFullDataQuery.Edge,String,Int)->Unit)
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
            listenerClick.invoke(character,
                (character.node?.image?.medium?: IMAGE_URL),position)

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