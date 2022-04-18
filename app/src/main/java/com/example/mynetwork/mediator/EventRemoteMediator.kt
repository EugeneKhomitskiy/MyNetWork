package com.example.mynetwork.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.mynetwork.api.EventApiService
import com.example.mynetwork.dao.EventDao
import com.example.mynetwork.dao.EventRemoteKeyDao
import com.example.mynetwork.db.AppDb
import com.example.mynetwork.entity.EventEntity
import com.example.mynetwork.entity.EventRemoteKeyEntity
import com.example.mynetwork.error.ApiError

@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator(
    private val eventApiService: EventApiService,
    private val eventDao: EventDao,
    private val eventRemoteKeyDao: EventRemoteKeyDao,
    private val appDb: AppDb
) : RemoteMediator<Int, EventEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> eventApiService.getLatest(state.config.pageSize)

                LoadType.PREPEND -> return MediatorResult.Success(true)

                LoadType.APPEND -> {
                    val id = eventRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    eventApiService.getBefore(id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                EventRemoteKeyEntity.KeyType.AFTER,
                                body.first().id
                            )
                        )
                        if (eventRemoteKeyDao.isEmpty()) {
                            eventRemoteKeyDao.insert(
                                EventRemoteKeyEntity(
                                    EventRemoteKeyEntity.KeyType.BEFORE,
                                    body.last().id
                                )
                            )
                        }
                    }
                    LoadType.APPEND -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                EventRemoteKeyEntity.KeyType.BEFORE,
                                body.last().id
                            )
                        )
                    }
                    LoadType.PREPEND -> eventRemoteKeyDao.insert(
                        EventRemoteKeyEntity(
                            EventRemoteKeyEntity.KeyType.AFTER,
                            body.first().id
                        )
                    )
                }
                eventDao.insertEvents(body.map(EventEntity::fromDto))
            }

            return MediatorResult.Success(body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}