package com.example.mynetwork.dto

data class User(
    var id: Long,
    val login: String,
    val name: String,
    val avatar: String? = null
)