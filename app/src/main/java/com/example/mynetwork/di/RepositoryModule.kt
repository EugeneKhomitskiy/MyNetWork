package com.example.mynetwork.di

import com.example.mynetwork.repository.*
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

    @Binds
    @Singleton
    fun bindsEventService(impl: EventRepositoryImpl): EventRepository

    @Binds
    @Singleton
    fun bindsUserService(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    fun bindsJobService(impl: JobRepositoryImpl): JobRepository

    @Binds
    @Singleton
    fun bindsSignInService(impl: SignInRepositoryImpl): SignInRepository

    @Binds
    @Singleton
    fun bindsSignUpService(impl: SignUpRepositoryImpl): SignUpRepository
}