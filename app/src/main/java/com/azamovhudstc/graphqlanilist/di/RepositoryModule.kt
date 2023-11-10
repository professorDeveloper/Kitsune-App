package com.azamovhudstc.graphqlanilist.di

import com.azamovhudstc.graphqlanilist.data.repository.DetailRepositoryImpl
import com.azamovhudstc.graphqlanilist.data.repository.FavoriteRepositoryImpl
import com.azamovhudstc.graphqlanilist.data.repository.SearchRepositoryImpl
import com.azamovhudstc.graphqlanilist.data.repository.UserRepositoryImpl
import com.azamovhudstc.graphqlanilist.domain.repository.DetailsRepository
import com.azamovhudstc.graphqlanilist.domain.repository.FavoriteRepository
import com.azamovhudstc.graphqlanilist.domain.repository.SearchRepository
import com.azamovhudstc.graphqlanilist.domain.repository.UserRepository
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
    abstract fun bindUserRepository(repository: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindFavoritesRepository(repository: FavoriteRepositoryImpl): FavoriteRepository

    @Binds
    abstract fun bindDetailsRepository(repository: DetailRepositoryImpl): DetailsRepository

    @Binds
    abstract fun bindSearchRepository(repository: SearchRepositoryImpl): SearchRepository
}