package com.example.mynetwork.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mynetwork.entity.UserIdEntity

@Dao
interface UserIdDao {

    @Query("SELECT id FROM UserIdEntity")
    suspend fun id(): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userId: UserIdEntity)

    @Query("DELETE FROM UserIdEntity")
    suspend fun removeAll()
}