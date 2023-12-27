/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.di

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.azamovhudstc.graphqlanilist.data.network.rest.api.JikanApi
import com.azamovhudstc.graphqlanilist.data.network.service.AniListGraphQlClient
import com.azamovhudstc.graphqlanilist.utils.Apollo
import com.azamovhudstc.graphqlanilist.utils.Constants.Companion.ANILIST_API_URL
import com.azamovhudstc.graphqlanilist.utils.Constants.Companion.JIKAN_API_URL
import com.azamovhudstc.graphqlanilist.utils.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module

object NetworkModule {

    @Provides
    @Singleton
    @RetrofitClient
    fun provideRetrofitOkHttpClient(
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        )
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        @RetrofitClient okHttpClient: OkHttpClient,
        @Named("base-url") url: String
    ): Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Named("base-url")
    fun provideBaseUrl(): String = JIKAN_API_URL

    @Provides
    @Singleton
    fun provideJikanApiService(retrofit: Retrofit): JikanApi =
        retrofit.create(JikanApi::class.java)


    @Provides
    @Singleton
    @Apollo
    fun provideOkHttpClient(
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()


    @Provides
    @Singleton
    fun provideApolloClient(
        @Apollo okHttpClient: OkHttpClient
    ): ApolloClient = ApolloClient.Builder()
        .serverUrl(ANILIST_API_URL)
        .okHttpClient(okHttpClient)
        .build()


    @Provides
    @Singleton
    fun provideAniListGraphQlClient(
        apolloClient: ApolloClient
    ): AniListGraphQlClient = AniListGraphQlClient(apolloClient)

}