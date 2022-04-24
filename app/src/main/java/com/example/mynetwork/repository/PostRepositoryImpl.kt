package com.example.mynetwork.repository

import androidx.paging.*
import com.example.mynetwork.api.PostApiService
import com.example.mynetwork.dao.PostDao
import com.example.mynetwork.dao.PostRemoteKeyDao
import com.example.mynetwork.db.AppDb
import com.example.mynetwork.dto.Attachment
import com.example.mynetwork.dto.Media
import com.example.mynetwork.dto.MediaUpload
import com.example.mynetwork.dto.Post
import com.example.mynetwork.entity.PostEntity
import com.example.mynetwork.enumeration.AttachmentType
import com.example.mynetwork.error.ApiError
import com.example.mynetwork.error.AppError
import com.example.mynetwork.error.NetworkError
import com.example.mynetwork.mediator.PostRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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
            //postDao.savePost(PostEntity.fromDto(post))
            val response = postApiService.savePost(post)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val data = response.body() ?: throw ApiError(response.message())
            postDao.insertPost(PostEntity.fromDto(data))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload, type: AttachmentType) {
        try {
            val media = upload(upload)
            val postWithAttachment =
                post.copy(attachment = Attachment(media.url, type))
            savePost(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", "name", upload.inputStream.readBytes()
                    .toRequestBody(
                        "*/*".toMediaType()
                    )
            )
            val response = postApiService.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            return response.body() ?: throw ApiError(response.message())
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            postDao.removeById(id)
            val response = postApiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            postDao.likeById(id)
            val response = postApiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val data = response.body() ?: throw ApiError(response.message())
            postDao.insertPost(PostEntity.fromDto(data))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun dislikeById(id: Long) {
        try {
            postDao.dislikeById(id)
            val response = postApiService.dislikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val data = response.body() ?: throw ApiError(response.message())
            postDao.insertPost(PostEntity.fromDto(data))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }
}