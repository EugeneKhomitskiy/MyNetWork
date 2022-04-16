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

    @Query(
        """
           UPDATE WallEntity SET
               `likeOwnerIds` = `likeOwnerIds` + 1,
               likedByMe = 1
           WHERE id = :id AND likedByMe = 0;
        """,
    )
    suspend fun likeById(id: Long)

    @Query(
        """
           UPDATE WallEntity SET
               `likeOwnerIds` = `likeOwnerIds` - 1,
               likedByMe = 0
           WHERE id = :id AND likedByMe = 1;
        """,
    )
    suspend fun dislikeById(id: Long)
}