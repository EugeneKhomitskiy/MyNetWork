package com.example.mynetwork.model

data class ModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
    val errorLogin: Boolean = false
)