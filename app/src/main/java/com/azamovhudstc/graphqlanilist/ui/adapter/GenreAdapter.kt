package com.azamovhudstc.graphqlanilist.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.graphqlanilist.databinding.ItemGenreBinding
import com.azamovhudstc.graphqlanilist.utils.randomColor

class GenreAdapter(
    private val type: String,
    private val genres: List<String?>
) : RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val binding = ItemGenreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GenreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val binding = holder.binding
        binding.genreTitle.text = genres[position]
//        binding.genreImage.loadImage(genre)
        binding.root.setCardBackgroundColor(randomColor())
    }




    override fun getItemCount(): Int = genres.size
    inner class GenreViewHolder(val binding: ItemGenreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {

            }
        }
    }


}