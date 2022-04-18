package com.example.mynetwork.repository

import androidx.paging.PagingData
import com.example.mynetwork.dto.Event
import com.example.mynetwork.dto.Media
import com.example.mynetwork.dto.MediaUpload
import kotlinx.coroutines.flow.Flow

interface EventRepository {

    val data : Flow<PagingData<Event>>

    suspend fun save(event: Event)
    suspend fun saveWithAttachment(event: Event, upload: MediaUpload)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun participate(id: Long)
    suspend fun notParticipate(id: Long)
}