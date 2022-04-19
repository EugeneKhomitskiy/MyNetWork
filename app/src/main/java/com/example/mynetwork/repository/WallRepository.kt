package com.example.mynetwork.repository

import androidx.paging.PagingData
import com.example.mynetwork.dto.Post
import kotlinx.coroutines.flow.Flow

interface WallRepository {
    fun userWall(userId: Long): Flow<PagingData<Post>>
}