package com.example.mynetwork.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.mynetwork.api.WallApiService
import com.example.mynetwork.dao.*
import com.example.mynetwork.db.AppDb
import com.example.mynetwork.entity.PostEntity
import com.example.mynetwork.entity.PostRemoteKeyEntity
import com.example.mynetwork.error.ApiError

@OptIn(ExperimentalPagingApi::class)
class WallRemoteMediator(
    private val wallApiService: WallApiService,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb,
    private val userId: Long
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {

            val response = when (loadType) {
                LoadType.REFRESH -> wallApiService.getWallLatest(userId, state.config.pageSize)

                LoadType.PREPEND -> return MediatorResult.Success(true)

                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    wallApiService.getWallBefore(userId, id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        postRemoteKeyDao.removeAll()
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.AFTER,
                                body.first().id
                            )
                        )
                        if (postRemoteKeyDao.isEmpty()) {
                            postRemoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.BEFORE,
                                    body.last().id
                                )
                            )
                        }
                    }
                    LoadType.APPEND -> postRemoteKeyDao.insert(
                        PostRemoteKeyEntity(
                            PostRemoteKeyEntity.KeyType.BEFORE,
                            body.last().id
                        )
                    )

                    LoadType.PREPEND -> postRemoteKeyDao.insert(
                        PostRemoteKeyEntity(
                            PostRemoteKeyEntity.KeyType.AFTER,
                            body.first().id
                        )
                    )
                }
                postDao.insertPosts(body.map(PostEntity::fromDto))
            }

            return MediatorResult.Success(body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}