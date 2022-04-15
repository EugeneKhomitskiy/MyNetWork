package com.example.mynetwork.repository

import androidx.paging.*
import com.example.mynetwork.api.WallApiService
import com.example.mynetwork.dao.*
import com.example.mynetwork.db.AppDb
import com.example.mynetwork.dto.Post
import com.example.mynetwork.entity.WallEntity
import com.example.mynetwork.mediator.WallRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WallRepositoryImpl @Inject constructor(
    private val wallDao: WallDao,
    wallApiService: WallApiService,
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
}