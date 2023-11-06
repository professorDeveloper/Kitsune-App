package com.azamovhudstc.graphqlanilist.di

import com.azamovhudstc.graphqlanilist.data.repository.SearchRepositoryImpl
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
    abstract fun bindSearchRepository(repository: SearchRepositoryImpl): SearchRepository
}