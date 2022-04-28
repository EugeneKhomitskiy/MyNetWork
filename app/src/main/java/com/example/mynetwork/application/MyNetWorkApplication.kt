package com.example.mynetwork.application

import com.example.mynetwork.auth.AppAuth
import android.app.Application
import com.example.mynetwork.BuildConfig
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyNetWorkApplication : Application() {
    private val appScope = CoroutineScope(Dispatchers.Default)

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreate() {
        super.onCreate()
        setupAuth()
        MapKitFactory.setApiKey(BuildConfig.MAPS_API_KEY)
    }

    private fun setupAuth() {
        appScope.launch {
            appAuth.sendPushToken()
        }
    }
}