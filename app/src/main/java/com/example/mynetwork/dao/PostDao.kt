package com.example.mynetwork.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.mynetwork.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, PostEntity>

    @Query("SELECT * FROM PostEntity WHERE authorId = :authorId ORDER BY id DESC")
    fun getPagingSource(authorId: Long): PagingSource<Int, PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(postEntity: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContent(id: Long, content: String)

    suspend fun savePost(postEntity: PostEntity) =
        if (postEntity.id == 0L) insertPost(postEntity) else updateContent(
            postEntity.id,
            postEntity.content
        )

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query(
        """
           UPDATE PostEntity SET
               `likeOwnerIds` = `likeOwnerIds` + 1,
               likedByMe = 1
           WHERE id = :id AND likedByMe = 0;
        """,
    )
    suspend fun likeById(id: Long)

    @Query(
        """
           UPDATE PostEntity SET
               `likeOwnerIds` = `likeOwnerIds` - 1,
               likedByMe = 0
           WHERE id = :id AND likedByMe = 1;
        """,
    )
    suspend fun dislikeById(id: Long)

    @Query(
        """
           UPDATE PostEntity SET
               `mentionIds` = `mentionIds` + 1
           WHERE id = :id;
        """,
    )
    suspend fun mentionById(id: Long)

    @Query("DELETE FROM PostEntity")
    suspend fun removeAll()
}