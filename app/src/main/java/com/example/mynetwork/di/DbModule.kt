package com.example.mynetwork.di

import android.content.Context
import androidx.room.Room
import com.example.mynetwork.dao.Converters
import com.example.mynetwork.dao.PostDao
import com.example.mynetwork.dao.PostRemoteKeyDao
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
    fun providesPostKeyDao(appDb: AppDb): PostRemoteKeyDao = appDb.postRemoteKeyDao()
}