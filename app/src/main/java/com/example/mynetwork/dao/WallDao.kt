package com.example.mynetwork.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.mynetwork.entity.PostEntity
import com.example.mynetwork.entity.WallEntity

@Dao
interface WallDao {
    @Query("SELECT * FROM WallEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, WallEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(wallEntity: WallEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<WallEntity>)

    @Query("UPDATE WallEntity SET content = :content WHERE id = :id")
    suspend fun updateContent(id: Long, content: String)

    @Query("DELETE FROM WallEntity")
    suspend fun removeAll()

    @Query("DELETE FROM WallEntity WHERE id = :id")
    suspend fun removeById(id: Long)
}