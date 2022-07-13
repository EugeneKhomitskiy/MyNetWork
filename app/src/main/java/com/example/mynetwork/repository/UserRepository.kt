package com.example.mynetwork.repository

import com.example.mynetwork.dto.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val data : Flow<List<User>>

    suspend fun getAll()
    suspend fun getUserById(id: Long): User
}