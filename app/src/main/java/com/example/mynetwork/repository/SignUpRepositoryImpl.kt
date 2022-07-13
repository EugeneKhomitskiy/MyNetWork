package com.example.mynetwork.repository

import android.net.Uri
import androidx.core.net.toFile
import com.example.mynetwork.api.UserApiService
import com.example.mynetwork.dto.PhotoUpload
import com.example.mynetwork.dto.Token
import com.example.mynetwork.error.ApiError
import com.example.mynetwork.error.NetworkError
import com.example.mynetwork.model.PhotoModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class SignUpRepositoryImpl @Inject constructor(
    private val userApiService: UserApiService
): SignUpRepository {

    override suspend fun registerUser(name: String, login: String, pass: String, uri: Uri): Token {
        try {
            val response = userApiService.registerUser(
                name.toRequestBody("text/plain".toMediaType()),
                login.toRequestBody("text/plain".toMediaType()),
                pass.toRequestBody("text/plain".toMediaType()),
                uri.toFile().let {
                    val upload = PhotoUpload(it)
                    MultipartBody.Part.createFormData(
                        "file", upload.file.name, upload.file.asRequestBody()
                    )
                }
            )
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