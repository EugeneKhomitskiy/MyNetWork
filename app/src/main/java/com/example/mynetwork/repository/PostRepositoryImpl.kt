package com.example.mynetwork.repository

import androidx.paging.*
import com.example.mynetwork.api.PostApiService
import com.example.mynetwork.dao.PostDao
import com.example.mynetwork.dao.PostRemoteKeyDao
import com.example.mynetwork.db.AppDb
import com.example.mynetwork.dto.Post
import com.example.mynetwork.entity.PostEntity
import com.example.mynetwork.error.ApiError
import com.example.mynetwork.error.NetworkError
import com.example.mynetwork.mediator.PostRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val postApiService: PostApiService,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = PostRemoteMediator(postApiService, postDao, postRemoteKeyDao, appDb),
        pagingSourceFactory = { postDao.getPagingSource() },
    )
        .flow
        .map { it.map(PostEntity::toDto) }

    override suspend fun savePost(post: Post) {
        try {
            postDao.savePost(PostEntity.fromDto(post))
            val response = postApiService.savePost(post)
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