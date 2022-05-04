package com.example.mynetwork.di

import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class FirebaseMessagingModule {

    @Singleton
    @Provides
    fun providesFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()

    @Singleton
    @Provides
    fun checkGoogleApiAvailability(): GoogleApiAvailabilityLight = GoogleApiAvailabilityLight.getInstance()
}