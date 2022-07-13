package com.example.mynetwork.repository

import com.example.mynetwork.api.UserApiService
import com.example.mynetwork.dto.Token
import com.example.mynetwork.error.ApiError
import com.example.mynetwork.error.NetworkError
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class SignInRepositoryImpl @Inject constructor(
    private val userApiService: UserApiService
): SignInRepository {

    override suspend fun updateUser(name: String, pass: String): Token {
        try {
            val response = userApiService.updateUser(name, pass)
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
}