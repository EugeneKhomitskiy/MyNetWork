package com.example.mynetwork.repository

import androidx.lifecycle.LiveData
import com.example.mynetwork.dto.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val data : Flow<List<Post>>

    suspend fun getAllPosts()
    suspend fun savePost(post: Post)
}