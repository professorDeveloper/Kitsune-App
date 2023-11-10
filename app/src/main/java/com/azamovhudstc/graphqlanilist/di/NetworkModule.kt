package com.azamovhudstc.graphqlanilist.di

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.azamovhudstc.graphqlanilist.data.local.*
import com.azamovhudstc.graphqlanilist.data.network.service.AniListGraphQlClient
import com.azamovhudstc.graphqlanilist.data.service.GogoAnimeApiClient
import com.azamovhudstc.graphqlanilist.data.singleton.ApiServiceSingleton
import com.azamovhudstc.graphqlanilist.domain.repository.PersistenceRepository
import com.azamovhudstc.graphqlanilist.parser.GoGoParser
import com.azamovhudstc.graphqlanilist.utils.Apollo
import com.azamovhudstc.graphqlanilist.utils.Constants.Companion.ANILIST_API_URL
import com.azamovhudstc.graphqlanilist.utils.Constants.Companion.GOGO_BASE_URL
import com.azamovhudstc.graphqlanilist.utils.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module

object NetworkModule {


    @Provides
    @Singleton
    @Apollo
    fun provideOkHttpClient(
        localStorage: PersistenceRepository,
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
    @RetrofitClient
    fun provideRetrofitOkHttpClient(
        settings: Settings
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
    @Singleton
    fun provideApolloClient(
        @Apollo okHttpClient: OkHttpClient
    ): ApolloClient = ApolloClient.Builder()
        .serverUrl(ANILIST_API_URL)
        .okHttpClient(okHttpClient)
        .build()

    @Provides
    @Named("base-url")
    fun provideBaseUrl(): String = GOGO_BASE_URL

    @Provides
    @Singleton
    fun provideApiServiceSingleton(
        @Named("base-url") baseUrlProvider: Provider<String>,
        @RetrofitClient okHttpClient: OkHttpClient,
        settings: Settings
    ) = ApiServiceSingleton(baseUrlProvider, okHttpClient, settings)


    @Provides
    @Singleton
    @IntoMap
    @StringKey("GOGO_ANIME")
    fun provideGogoAnimeApiClient(client: GogoAnimeApiClient): BaseClient = client

    @Provides
    @Singleton
    @IntoMap
    @StringKey("GOGO_ANIME_PARSER")
    fun provideGoGoAnimeParser(parser: GoGoParser): BaseParser = parser
    @Provides
    @Singleton
    fun provideUpdateClient(service: UpdateService): UpdateClient = UpdateClient(service)

    @Provides
    @Singleton
    fun provideUpdateService(retrofit: Retrofit): UpdateService =
        retrofit.create(UpdateService::class.java)

    @Provides
    @Singleton
    fun provideAniListGraphQlClient(
        apolloClient: ApolloClient
    ): AniListGraphQlClient = AniListGraphQlClient(apolloClient)

}