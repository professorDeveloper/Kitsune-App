package com.azamovhudstc.graphqlanilist.di

import android.app.Application
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object VideoPlayerModule {

    @Provides
    @ViewModelScoped
    fun provideVideoPlayer(app: Application): ExoPlayer {
        return ExoPlayer.Builder(app)
            .setSeekForwardIncrementMs(10000)
            .setSeekBackIncrementMs(10000)
            .build()
    }

}