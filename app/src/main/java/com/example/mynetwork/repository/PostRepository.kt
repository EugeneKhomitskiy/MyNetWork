package com.example.mynetwork.repository

import androidx.paging.PagingData
import com.example.mynetwork.dto.Media
import com.example.mynetwork.dto.MediaUpload
import com.example.mynetwork.dto.Post
import com.example.mynetwork.enumeration.AttachmentType
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val data: Flow<PagingData<Post>>

    suspend fun savePost(post: Post)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload, type: AttachmentType)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
}