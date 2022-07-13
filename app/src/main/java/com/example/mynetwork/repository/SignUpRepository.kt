package com.example.mynetwork.repository

import android.net.Uri
import com.example.mynetwork.dto.Token
import com.example.mynetwork.model.PhotoModel

interface SignUpRepository {

    suspend fun registerUser(name: String, login: String, pass: String, uri: Uri): Token
}