package com.example.mynetwork.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.mynetwork.api.WallApiService
import com.example.mynetwork.dao.*
import com.example.mynetwork.db.AppDb
import com.example.mynetwork.entity.WallEntity
import com.example.mynetwork.entity.WallRemoteKeyEntity
import com.example.mynetwork.error.ApiError

@OptIn(ExperimentalPagingApi::class)
class WallRemoteMediator(
    private val wallApiService: WallApiService,
    private val wallDao: WallDao,
    private val wallRemoteKeyDao: WallRemoteKeyDao,
    private val appDb: AppDb,
    private val userIdDao: UserIdDao
) : RemoteMediator<Int, WallEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, WallEntity>
    ): MediatorResult {
        try {

            val userid = userIdDao.id()

            val response = when (loadType) {
                LoadType.REFRESH -> wallApiService.getWallLatest(userid, state.config.initialLoadSize
                )

                LoadType.PREPEND -> {
                    val id = wallRemoteKeyDao.max() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    wallApiService.getWallAfter(userid, id, state.config.pageSize)
                }
                LoadType.APPEND -> {
                    val id = wallRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    wallApiService.getWallBefore(userid, id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        wallRemoteKeyDao.insert(
                            WallRemoteKeyEntity(
                                WallRemoteKeyEntity.KeyType.AFTER,
                                body.first().id
                            )
                        )
                        if (wallRemoteKeyDao.isEmpty()) {
                            wallRemoteKeyDao.insert(
                                WallRemoteKeyEntity(
                                    WallRemoteKeyEntity.KeyType.BEFORE,
                                    body.last().id
                                )
                            )
                        }
                    }
                    LoadType.APPEND -> {
                        wallRemoteKeyDao.insert(
                            WallRemoteKeyEntity(
                                WallRemoteKeyEntity.KeyType.BEFORE,
                                body.last().id
                            )
                        )
                    }
                    LoadType.PREPEND -> wallRemoteKeyDao.insert(
                        WallRemoteKeyEntity(
                            WallRemoteKeyEntity.KeyType.AFTER,
                            body.first().id
                        )
                    )
                }
                wallDao.insertPosts(body.map(WallEntity::fromDto))
            }

            return MediatorResult.Success(body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}