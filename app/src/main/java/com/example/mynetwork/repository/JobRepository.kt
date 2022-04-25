package com.example.mynetwork.repository

import com.example.mynetwork.dto.Job
import kotlinx.coroutines.flow.Flow

interface JobRepository {
    val data : Flow<List<Job>>

    suspend fun save(job: Job)
    suspend fun getByUserId(id: Long)
    suspend fun removeById(id: Long)
}