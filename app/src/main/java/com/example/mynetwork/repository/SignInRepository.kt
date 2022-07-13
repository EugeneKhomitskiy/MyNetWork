package com.example.mynetwork.repository

import com.example.mynetwork.dto.Token

interface SignInRepository {

    suspend fun updateUser(name: String, pass: String): Token
}