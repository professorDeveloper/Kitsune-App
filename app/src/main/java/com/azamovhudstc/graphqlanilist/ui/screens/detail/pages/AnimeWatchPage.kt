package com.azamovhudstc.graphqlanilist.ui.screens.detail.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.azamovhudstc.graphqlanilist.R
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.data.model.ui_models.Media
import com.azamovhudstc.graphqlanilist.databinding.FragmentAnimeWatchPageBinding

class AnimeWatchPage(private val aniListMedia: AniListMedia, private val media: Media) : Fragment() {

    private var _binding :FragmentAnimeWatchPageBinding?=null
    private val binding get()=_binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAnimeWatchPageBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}