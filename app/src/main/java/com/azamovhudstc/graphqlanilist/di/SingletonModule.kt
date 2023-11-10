package com.azamovhudstc.graphqlanilist.di

import com.azamovhudstc.graphqlanilist.data.repository.PersistenceRepositoryImpl
import com.azamovhudstc.graphqlanilist.domain.repository.PersistenceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SingletonModule {
    @Binds
    abstract fun bindPersistenceRepository(repository: PersistenceRepositoryImpl): PersistenceRepository

}
