package com.example.mynetwork.model

import com.example.mynetwork.dto.Post

data class PostModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false
)

data class ModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
    val errorLogin: Boolean = false
)