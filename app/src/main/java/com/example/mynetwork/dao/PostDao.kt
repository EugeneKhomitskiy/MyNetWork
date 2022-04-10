package com.example.mynetwork.dao

import androidx.room.*
import com.example.mynetwork.dto.Post
import com.example.mynetwork.entity.PostEntity
import com.example.mynetwork.enumeration.AttachmentType
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(postEntity: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContent(id: Long, content: String)

    suspend fun savePost(postEntity: PostEntity) =
        if (postEntity.id == 0L) insertPost(postEntity) else updateContent(postEntity.id, postEntity.content)
}

class Converters {
    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)

    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name

    @TypeConverter
    fun fromSet(set: Set<Long>): String = set.joinToString(",")

    @TypeConverter
    fun toSet(data: String): Set<Long> = data.split(",").map { it.toLong() }.toSet()
}