package com.example.mynetwork.repository

import androidx.paging.*
import com.example.mynetwork.api.WallApiService
import com.example.mynetwork.dao.*
import com.example.mynetwork.db.AppDb
import com.example.mynetwork.dto.Post
import com.example.mynetwork.entity.PostEntity
import com.example.mynetwork.mediator.WallRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WallRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val wallApiService: WallApiService,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb
) : WallRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun userWall(userId: Long): Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = WallRemoteMediator(wallApiService, postDao, postRemoteKeyDao, appDb, userId),
        pagingSourceFactory = { postDao.getPagingSource(userId) },
    )
        .flow
        .map { it.map(PostEntity::toDto) }
}