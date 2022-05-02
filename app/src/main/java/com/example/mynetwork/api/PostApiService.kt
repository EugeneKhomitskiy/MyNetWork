package com.example.mynetwork.api

import com.example.mynetwork.dto.Media
import com.example.mynetwork.dto.Post
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PostApiService {

    @GET("posts/{id}/before")
    suspend fun getBefore(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>

    @POST("posts")
    suspend fun savePost(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<Media>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>
}