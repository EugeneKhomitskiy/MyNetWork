package com.example.mynetwork.repository

import androidx.paging.*
import com.example.mynetwork.api.WallApiService
import com.example.mynetwork.dao.*
import com.example.mynetwork.db.AppDb
import com.example.mynetwork.dto.Post
import com.example.mynetwork.entity.WallEntity
import com.example.mynetwork.entity.toWallEntity
import com.example.mynetwork.error.ApiError
import com.example.mynetwork.error.NetworkError
import com.example.mynetwork.mediator.WallRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class WallRepositoryImpl @Inject constructor(
    private val wallDao: WallDao,
    private val wallApiService: WallApiService,
    wallRemoteKeyDao: WallRemoteKeyDao,
    appDb: AppDb,
    userIdDao: UserIdDao
) : WallRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = WallRemoteMediator(wallApiService, wallDao, wallRemoteKeyDao, appDb, userIdDao),
        pagingSourceFactory = { wallDao.getPagingSource() },
    )
        .flow
        .map { it.map(WallEntity::toDto) }

    override suspend fun load(id: Long) {
        try {
            val response = wallApiService.getWallLatest(id, 10)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val data = response.body() ?: throw ApiError(response.message())
            wallDao.insertPosts(data.toWallEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }
}