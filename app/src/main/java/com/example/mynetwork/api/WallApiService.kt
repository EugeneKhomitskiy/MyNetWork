package com.example.mynetwork.api

import com.example.mynetwork.dto.Post
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WallApiService {

    @GET("{userId}/wall/{id}/before")
    suspend fun getWallBefore(
        @Path("userId") userId: Long,
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("{userId}/wall/latest")
    suspend fun getWallLatest(
        @Path("userId") userId: Long,
        @Query("count") count: Int
    ): Response<List<Post>>
}