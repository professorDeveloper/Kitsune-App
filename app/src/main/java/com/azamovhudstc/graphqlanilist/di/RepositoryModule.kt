package com.azamovhudstc.graphqlanilist.di

import com.azamovhudstc.graphqlanilist.data.repository.CharacterRepositoryImpl
import com.azamovhudstc.graphqlanilist.data.repository.DetailRepositoryImpl
import com.azamovhudstc.graphqlanilist.data.repository.EpisodesRepositoryImpl
import com.azamovhudstc.graphqlanilist.data.repository.SearchRepositoryImpl
import com.azamovhudstc.graphqlanilist.domain.repository.CharacterRepository
import com.azamovhudstc.graphqlanilist.domain.repository.DetailRepository
import com.azamovhudstc.graphqlanilist.domain.repository.EpisodesRepository
import com.azamovhudstc.graphqlanilist.domain.repository.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
@InstallIn(ViewModelComponent::class)
@OptIn(ExperimentalCoroutinesApi::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindDetailRepository(repository: DetailRepositoryImpl):DetailRepository

    @Binds
    abstract fun bindSearchRepository(repository: SearchRepositoryImpl): SearchRepository

    @Binds
    abstract fun bindCharacterRepository(repository: CharacterRepositoryImpl): CharacterRepository

    @Binds
    abstract fun binEpisodesRepository(repository: EpisodesRepositoryImpl): EpisodesRepository

}