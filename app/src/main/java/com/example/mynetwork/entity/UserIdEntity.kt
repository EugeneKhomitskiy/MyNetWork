package com.example.mynetwork.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserIdEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long
)