package com.example.mynetwork.di

import com.example.mynetwork.repository.PostRepository
import com.example.mynetwork.repository.PostRepositoryImpl
import com.example.mynetwork.repository.WallRepository
import com.example.mynetwork.repository.WallRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindsPostService(impl: PostRepositoryImpl): PostRepository

    @Binds
    @Singleton
    fun bindsWallService(impl: WallRepositoryImpl): WallRepository
}