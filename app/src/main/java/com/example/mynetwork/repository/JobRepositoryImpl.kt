package com.example.mynetwork.repository

import com.example.mynetwork.api.JobApiService
import com.example.mynetwork.dao.JobDao
import com.example.mynetwork.dto.Job
import com.example.mynetwork.entity.JobEntity
import com.example.mynetwork.entity.PostEntity
import com.example.mynetwork.entity.toDto
import com.example.mynetwork.entity.toJobEntity
import com.example.mynetwork.error.ApiError
import com.example.mynetwork.error.NetworkError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(
    private val jobDao: JobDao,
    private val jobApiService: JobApiService
) : JobRepository {
    override val data: Flow<List<Job>> = jobDao.get()
        .map { it.toDto() }
        .flowOn(Dispatchers.Default)

    override suspend fun save(job: Job) {
        try {
            jobDao.save(JobEntity.fromDto(job))
            val response = jobApiService.save(job)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val data = response.body() ?: throw ApiError(response.message())
            jobDao.insert(JobEntity.fromDto(data))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun getByUserId(id: Long) {
        try {
            jobDao.removeAll()
            val response = jobApiService.getByUserId(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val data = response.body() ?: throw ApiError(response.message())
            jobDao.insert(data.toJobEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            jobDao.removeById(id)
            val response = jobApiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }
}