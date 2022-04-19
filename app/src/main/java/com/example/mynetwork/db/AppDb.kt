package com.example.mynetwork.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mynetwork.dao.*
import com.example.mynetwork.entity.*

@Database(
    entities = [PostEntity::class,
        PostRemoteKeyEntity::class,
        EventRemoteKeyEntity::class,
        EventEntity::class,
        UserIdEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract val postDao: PostDao
    abstract val eventDao: EventDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
    abstract fun userIdDao(): UserIdDao
}