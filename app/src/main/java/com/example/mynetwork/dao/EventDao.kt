package com.example.mynetwork.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mynetwork.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM EventEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(eventEntity: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(posts: List<EventEntity>)

    @Query("UPDATE EventEntity SET content = :content WHERE id = :id")
    suspend fun updateContent(id: Long, content: String)

    suspend fun saveEvent(eventEntity: EventEntity) =
        if (eventEntity.id == 0L) insertEvent(eventEntity) else updateContent(
            eventEntity.id,
            eventEntity.content
        )

    @Query("DELETE FROM EventEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query(
        """
           UPDATE EventEntity SET
               `likeOwnerIds` = `likeOwnerIds` + 1,
               likedByMe = 1
           WHERE id = :id AND likedByMe = 0;
        """,
    )
    suspend fun likeById(id: Long)

    @Query(
        """
           UPDATE EventEntity SET
               `likeOwnerIds` = `likeOwnerIds` - 1,
               likedByMe = 0
           WHERE id = :id AND likedByMe = 1;
        """,
    )
    suspend fun dislikeById(id: Long)

    @Query(
        """
           UPDATE EventEntity SET
               `participantsIds` = `participantsIds` + 1,
               participatedByMe = 1
           WHERE id = :id AND participatedByMe = 0;
        """,
    )
    suspend fun participate(id: Long)

    @Query(
        """
           UPDATE EventEntity SET
               `participantsIds` = `participantsIds` - 1,
               participatedByMe = 0
           WHERE id = :id AND participatedByMe = 1;
        """,
    )
    suspend fun notParticipate(id: Long)

    @Query(
        """
           UPDATE EventEntity SET
               `speakerIds` = `speakerIds` + 1
           WHERE id = :id;
        """,
    )
    suspend fun speakerById(id: Long)
}