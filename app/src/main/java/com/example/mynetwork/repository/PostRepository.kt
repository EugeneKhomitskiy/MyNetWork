package com.example.mynetwork.repository

import androidx.paging.PagingData
import com.example.mynetwork.dto.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val data : Flow<PagingData<Post>>

    suspend fun savePost(post: Post)
}