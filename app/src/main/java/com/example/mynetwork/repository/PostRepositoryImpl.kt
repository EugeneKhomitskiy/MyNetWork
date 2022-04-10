package com.example.mynetwork.repository

import com.example.mynetwork.api.ApiService
import com.example.mynetwork.dao.PostDao
import com.example.mynetwork.dto.Post
import com.example.mynetwork.entity.PostEntity
import com.example.mynetwork.entity.toDto
import com.example.mynetwork.entity.toEntity
import com.example.mynetwork.error.ApiError
import com.example.mynetwork.error.NetworkError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService
) : PostRepository {

    override val data: Flow<List<Post>> = postDao.getAllPosts()
        .map { it.toDto() }
        .flowOn(Dispatchers.Default)

    override suspend fun getAllPosts() {
        try {
            postDao.getAllPosts()
            val response = apiService.getAllPosts()
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val data = response.body() ?: throw ApiError(response.message())
            postDao.insertPosts(data.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun savePost(post: Post) {
        try {
            postDao.savePost(PostEntity.fromDto(post))
            val response = apiService.savePost(post)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())
            postDao.insertPost(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

}