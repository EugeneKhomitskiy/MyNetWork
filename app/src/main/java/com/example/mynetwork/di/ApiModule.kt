package com.example.mynetwork.di

import com.example.mynetwork.BuildConfig
import com.example.mynetwork.api.*
import com.example.mynetwork.auth.AppAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    companion object {
        private const val BASE_URL = "${BuildConfig.BASE_URL}/api/"
    }

    @Singleton
    @Provides
    fun providesHttpLogging(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    fun providesOkHttp(
        appAuth: AppAuth,
        logging: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            appAuth.authStateFlow.value.token?.let { token ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", token)
                    .build()
                return@addInterceptor chain.proceed(newRequest)
            }
            chain.proceed(chain.request())
        }
        .build()

    @Singleton
    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun providesPostApiService(retrofit: Retrofit): PostApiService = retrofit.create()

    @Singleton
    @Provides
    fun providesUserApiService(retrofit: Retrofit): UserApiService = retrofit.create()

    @Singleton
    @Provides
    fun providesWallApiService(retrofit: Retrofit): WallApiService = retrofit.create()

    @Singleton
    @Provides
    fun providesEventApiService(retrofit: Retrofit): EventApiService = retrofit.create()

    @Singleton
    @Provides
    fun providesJobApiService(retrofit: Retrofit): JobApiService = retrofit.create()
}