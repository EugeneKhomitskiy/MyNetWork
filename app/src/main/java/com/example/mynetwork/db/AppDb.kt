package com.example.mynetwork.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mynetwork.dao.Converters
import com.example.mynetwork.dao.PostDao
import com.example.mynetwork.entity.PostEntity

@Database(entities = [PostEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract val postDao: PostDao
}