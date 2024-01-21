/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.azamovhudstc.graphqlanilist.R
import com.azamovhudstc.graphqlanilist.application.App
import com.azamovhudstc.graphqlanilist.databinding.DetailScreenBinding
import com.azamovhudstc.graphqlanilist.databinding.FragmentSplashScreenBinding
import com.azamovhudstc.graphqlanilist.utils.animationTransactionClearStack
import com.azamovhudstc.graphqlanilist.utils.slideUp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashScreen : Fragment(R.layout.fragment_splash_screen) {
    private var binding: FragmentSplashScreenBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
          requireActivity().window.statusBarColor=App.instance.getColor(R.color.colorPrimaryDark)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSplashScreenBinding.bind(view)

        lifecycleScope.launch {
            binding?.apply {
                splashContainer.slideUp(900,1)
                splashLogo.slideUp(900,1,)

            }
            delay(1300)
            findNavController().navigate(R.id.homeScreen,null, animationTransactionClearStack(R.id.splashScreen).build())
        }


    }
}