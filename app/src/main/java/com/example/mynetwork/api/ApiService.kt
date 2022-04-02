package com.example.mynetwork.api

import com.example.mynetwork.dto.PushToken
import com.example.mynetwork.dto.Token
import com.example.mynetwork.dto.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("users")
    suspend fun getUsers() : Response<List<User>>

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<Token>

    @Multipart
    @POST("users/registration")
    suspend fun registerUser(
        @Part("login") login: RequestBody,
        @Part("pass") pass: RequestBody,
        @Part("name") name: RequestBody,
        @Part file: MultipartBody.Part?
    ): Response<Token>

    @POST("users/push-tokens")
    suspend fun save(@Body pushToken: PushToken): Response<Unit>
}