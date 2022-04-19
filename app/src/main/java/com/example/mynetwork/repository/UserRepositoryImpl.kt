package com.example.mynetwork.repository

import com.example.mynetwork.api.UserApiService
import com.example.mynetwork.dao.UserDao
import com.example.mynetwork.dto.User
import com.example.mynetwork.entity.toDto
import com.example.mynetwork.entity.toUserEntity
import com.example.mynetwork.error.ApiError
import com.example.mynetwork.error.NetworkError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val userApiService: UserApiService
) : UserRepository {
    override val data: Flow<List<User>> = userDao.getAll()
        .map { it.toDto() }
        .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            userDao.getAll()
            val response = userApiService.getUsers()
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val data = response.body() ?: throw ApiError(response.message())
            userDao.insert(data.toUserEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }
}