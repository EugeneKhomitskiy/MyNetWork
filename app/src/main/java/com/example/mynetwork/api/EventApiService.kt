package com.example.mynetwork.api

import com.example.mynetwork.dto.Event
import com.example.mynetwork.dto.Media
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface EventApiService {

    @GET("events/{id}/before")
    suspend fun getBefore(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Event>>

    @GET("events/{id}/after")
    suspend fun getAfter(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Event>>

    @GET("events/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Event>>

    @POST("events")
    suspend fun save(@Body event: Event): Response<Event>

    @DELETE("events/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<Media>

    @POST("events/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Event>

    @DELETE("events/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Event>

    @POST("events/{id}/participants")
    suspend fun participate(@Path("id") id: Long): Response<Event>

    @DELETE("events/{id}/participants")
    suspend fun notParticipate(@Path("id") id: Long): Response<Event>
}