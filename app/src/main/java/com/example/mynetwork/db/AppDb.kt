package com.example.mynetwork.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mynetwork.dao.*
import com.example.mynetwork.entity.*

@Database(
    entities = [PostEntity::class,
        WallEntity::class,
        PostRemoteKeyEntity::class,
        WallRemoteKeyEntity::class,
        UserIdEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract val postDao: PostDao
    abstract val wallDao: WallDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun wallRemoteKeyDao(): WallRemoteKeyDao
    abstract fun userIdDao(): UserIdDao
}