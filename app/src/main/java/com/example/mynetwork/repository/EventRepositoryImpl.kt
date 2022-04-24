package com.example.mynetwork.repository

import androidx.paging.*
import com.example.mynetwork.api.EventApiService
import com.example.mynetwork.dao.*
import com.example.mynetwork.db.AppDb
import com.example.mynetwork.dto.Attachment
import com.example.mynetwork.dto.Event
import com.example.mynetwork.dto.Media
import com.example.mynetwork.dto.MediaUpload
import com.example.mynetwork.entity.EventEntity
import com.example.mynetwork.enumeration.AttachmentType
import com.example.mynetwork.error.ApiError
import com.example.mynetwork.error.AppError
import com.example.mynetwork.error.NetworkError
import com.example.mynetwork.mediator.EventRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val eventApiService: EventApiService,
    eventRemoteKeyDao: EventRemoteKeyDao,
    appDb: AppDb
) : EventRepository {
    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Event>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = EventRemoteMediator(eventApiService, eventDao, eventRemoteKeyDao, appDb),
        pagingSourceFactory = { eventDao.getPagingSource() },
    )
        .flow
        .map { it.map(EventEntity::toDto) }

    override suspend fun save(event: Event) {
        try {
            eventDao.saveEvent(EventEntity.fromDto(event))
            val response = eventApiService.save(event)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val data = response.body() ?: throw ApiError(response.message())
            eventDao.insertEvent(EventEntity.fromDto(data))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun saveWithAttachment(event: Event, upload: MediaUpload) {
        try {
            val media = upload(upload)
            val eventWithAttachment =
                event.copy(attachment = Attachment(media.url, AttachmentType.IMAGE))
            save(eventWithAttachment)
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
                        "*/*".toMediaTypeOrNull()
                    )
            )
            val response = eventApiService.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            return response.body() ?: throw ApiError(response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            eventDao.removeById(id)
            val response = eventApiService.removeById(id)
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
            eventDao.likeById(id)
            val response = eventApiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val data = response.body() ?: throw ApiError(response.message())
            eventDao.insertEvent(EventEntity.fromDto(data))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun dislikeById(id: Long) {
        try {
            eventDao.dislikeById(id)
            val response = eventApiService.dislikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val data = response.body() ?: throw ApiError(response.message())
            eventDao.insertEvent(EventEntity.fromDto(data))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun participate(id: Long) {
        try {
            eventDao.participate(id)
            val response = eventApiService.participate(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val data = response.body() ?: throw ApiError(response.message())
            eventDao.insertEvent(EventEntity.fromDto(data))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun notParticipate(id: Long) {
        try {
            eventDao.notParticipate(id)
            val response = eventApiService.notParticipate(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val data = response.body() ?: throw ApiError(response.message())
            eventDao.insertEvent(EventEntity.fromDto(data))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }
}