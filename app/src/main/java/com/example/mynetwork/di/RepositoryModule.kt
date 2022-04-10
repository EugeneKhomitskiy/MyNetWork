package com.example.mynetwork.di

import com.example.mynetwork.repository.PostRepository
import com.example.mynetwork.repository.PostRepositoryImpl
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
}