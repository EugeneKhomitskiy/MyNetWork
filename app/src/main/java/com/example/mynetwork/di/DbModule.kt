package com.example.mynetwork.di

import android.content.Context
import androidx.room.Room
import com.example.mynetwork.dao.*
import com.example.mynetwork.db.AppDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DbModule {
    @Singleton
    @Provides
    fun provideDb(@ApplicationContext context: Context): AppDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
        .fallbackToDestructiveMigration()
        .build()
    @Provides
    fun providesPostDao(appDb: AppDb): PostDao = appDb.postDao
    @Provides
    fun providesEventDao(appDb: AppDb): EventDao = appDb.eventDao
    @Provides
    fun providesPostKeyDao(appDb: AppDb): PostRemoteKeyDao = appDb.postRemoteKeyDao()
    @Provides
    fun providesEventKeyDao(appDb: AppDb): EventRemoteKeyDao = appDb.eventRemoteKeyDao()
    @Provides
    fun providesUserDao(appDb: AppDb): UserDao = appDb.userDao()
    @Provides
    fun providesJobDao(appDb: AppDb): JobDao = appDb.jobDao()
}