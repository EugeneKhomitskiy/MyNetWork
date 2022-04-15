package com.example.mynetwork.repository

import androidx.paging.PagingData
import com.example.mynetwork.dto.Post
import kotlinx.coroutines.flow.Flow

interface WallRepository {
    val data : Flow<PagingData<Post>>
}